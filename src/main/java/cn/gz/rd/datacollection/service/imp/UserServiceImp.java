package cn.gz.rd.datacollection.service.imp;

import cn.gz.rd.datacollection.dao.*;
import cn.gz.rd.datacollection.model.*;
import cn.gz.rd.datacollection.service.IUserService;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImp implements IUserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    @Resource
    private UserMapper userMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Resource
    private UserMenuRelationTblMapper userMenuRelationTblMapper;

    @Resource
    private WorkDeptRelationTblMapper workDeptRelationTblMapper;

    @Resource
    private CodeParallelTableMapper codeParallelTableMapper;

    @Override
    public int countAll() {
        return userMapper.countAll();
    }

    @Override
    public int insert(User record) {
        return userMapper.insert(record);
    }

    @Override
    public User selectByAccount(String account) {
        return userMapper.selectByAccount(account);
    }

    @Override
    public User selectByAccountAndPw(User user) {
        User loginUser = userMapper.selectByAccountAndPw(user);
        if (loginUser == null) {
            return loginUser;
        }
        List<String> menuAuthorityList =
                userMenuRelationTblMapper.selectByUserId(loginUser.getId());
        loginUser.setMenuAuthroityList(menuAuthorityList);
        //获取用户角色
        String role = loginUser.getRole();
        //如果用户角色为部门, 则获取部门菜单权限并添加到登录用户信息中
        if (role.equals("部门")){
            int result = workDeptRelationTblMapper.selectBudgetWorkByDeptId(loginUser.getDepartmentId());
            if (result == 1){
                loginUser.setBatchUpload(true);
                String url = codeParallelTableMapper.selectUrlByCode(loginUser.getDepartmentId());
                loginUser.setCodeTableUrl(url);
            }else {
                loginUser.setBatchUpload(false);
            }
        }
        if (role.equals("工委")){
            if (loginUser.getWorkingCommitteeId().equals("GWBM_05")){
                loginUser.setBatchUpload(true);
                String url = codeParallelTableMapper.selectUrlByCode("GWBM_05");
                loginUser.setCodeTableUrl(url);
            }
        }
        return loginUser;
    }

    @Override
    public int insert(SysUser user) throws CommonException {
        logger.debug("插入用户：" + user.toString());
        String passwd = user.getPasswd();
        String codecPasswd = Hashing.md5().hashBytes(passwd.getBytes()).toString();
        user.setPasswd(codecPasswd);
        int count = sysUserMapper.insertSelective(user);
        Integer userId = user.getUserId();
        logger.debug("插入用户ID：" + userId);
        //获取菜单编码数组
        int[] menuIdArray = user.getMenuIdArray();
        if (menuIdArray != null && menuIdArray.length != 0){
            for (int menuId : menuIdArray){
                UserMenuRelationTbl userMenuRelationTbl = new UserMenuRelationTbl();
                userMenuRelationTbl.setUserId(user.getUserId());
                userMenuRelationTbl.setMenuId(menuId);
                int result = userMenuRelationTblMapper.insert(userMenuRelationTbl);
                if (result == 0){
                    throw new CommonException("新增用户所属菜单权限失败!");
                }
            }
        }
        return count;
    }

    @Override
    public int update(SysUser user) throws CommonException {
        logger.debug("更新用户：" + user.toString());
        List<Integer> newMenuIds = Ints.asList(user.getMenuIdArray());
        Integer userId = user.getUserId();
        String passwd = user.getPasswd();
        if (StringUtils.isNotEmpty(passwd)) {
            passwd = Hashing.md5().hashBytes(passwd.getBytes()).toString();
            user.setPasswd(passwd);
        } else {
            user.setPasswd(null);
        }
        int insertCount = sysUserMapper.updateByUserIdNotPasswd(user);
        logger.debug("更新用户的条数：" + insertCount);
        //更新部门所属菜单权限信息
        List<Integer> oldMenuIds = userMenuRelationTblMapper.selectMenuIdsByUserId(userId);
        List<Integer> removeMenuIds = new ArrayList<>(oldMenuIds);
        List<Integer> addMenuIds = new ArrayList<>(newMenuIds);
        for (int removeMenuId : removeMenuIds){
            //先删除部门所属菜单权限
            int result = userMenuRelationTblMapper.deleteByMenuIdAndUserId(removeMenuId, userId);
            if (result == 0){
                throw new CommonException("部门所属菜单权限删除失败!");
            }
        }
        //遍历插入部门所属菜单权限
        for (int addMenuId : addMenuIds){
            UserMenuRelationTbl deptMenuRelationTbl = new UserMenuRelationTbl();
            deptMenuRelationTbl.setUserId(userId);
            deptMenuRelationTbl.setMenuId(addMenuId);
            int result = userMenuRelationTblMapper.insert(deptMenuRelationTbl);
            if (result == 0){
                throw new CommonException("部门所属菜单权限新增失败!");
            }
        }
        return insertCount;
    }

    @Override
    public int delete(int userId) {
        logger.debug("删除用户：userID=" + userId);
        int deleteCount = sysUserMapper.deleteByPrimaryKey(userId);
        List<Integer> menuIds = userMenuRelationTblMapper.selectMenuIdsByUserId(userId);
        for (int menuId : menuIds){
            userMenuRelationTblMapper.deleteByMenuIdAndUserId(menuId, userId);
        }
        logger.debug("删除用户的条数：" + deleteCount);
        return deleteCount;
    }

    @Override
    public List<ShowUser> query(
            String userName, String nickName,
            Integer pageNum, Integer pageSize) {
        if (StringUtils.isNotBlank(userName)) {
            userName = "%" + userName + "%";
        } else {
            userName = null;
        }
        if (StringUtils.isNotBlank(nickName)) {
            nickName = "%" + nickName + "%";
        } else {
            nickName = null;
        }
        logger.debug("查询用户信息：userName = {}, nickName = {}, pageNum = {}, pageSize = {}",
                userName, nickName, pageNum, pageSize);
        List<ShowUser> showUsers = sysUserMapper.selectByPage(userName, nickName,
                pageSize * (pageNum - 1), pageSize);
        handleMenuAuthority(showUsers);
        logger.debug("查询用户信息结果：size=" + showUsers.size());
        return showUsers;
    }

    @Override
    public int count(String userName, String nickName) {
        if (StringUtils.isNotBlank(userName)) {
            userName = "%" + userName + "%";
        } else {
            userName = null;
        }
        if (StringUtils.isNotBlank(nickName)) {
            nickName = "%" + nickName + "%";
        } else {
            nickName = null;
        }
        logger.debug("查询用户总个数：userName = {}, nickName = {}",
                userName, nickName);
        int count = sysUserMapper.count(userName, nickName);
        logger.debug("查询用户总个数结果：" + count);
        return count;
    }

    @Override
    public boolean existSameUser(String userName) {
        logger.debug("是否存在相同的用户名：userName = {}", userName);
        int countSanmeUserName = sysUserMapper.countSanmeUserName(userName);
        logger.debug("相同的用户名个数=", countSanmeUserName);
        if (countSanmeUserName > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean existUserById(Integer id) {
        logger.debug("用户ID是否存在： id=" + id);
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(id);
        logger.debug("用户ID是否存在： 查询到的用户信息=" + sysUser);
        if (sysUser == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void chgPasswd(String userName, String oldPasswd, String newPasswd) {
        String codecOldPasswd = Hashing.md5().hashBytes(oldPasswd.getBytes()).toString();
        String codecNewPasswd = Hashing.md5().hashBytes(newPasswd.getBytes()).toString();

        User user = new User();
        user.setAccount(userName);
        user.setPassword(codecOldPasswd);
        User userDb = userMapper.selectByAccountAndPw(user);
        if (userDb == null) {
            throw new IllegalArgumentException("用户密码不正确！");
        }
        userMapper.updatePasswd(userName, codecNewPasswd);
    }

    /**
     * 获取每个用户的所属菜单权限列表, 用于展示
     * @param showUsers 用户信息列表
     */
    private void handleMenuAuthority(List<ShowUser> showUsers){
        String menuAuthority;
        //遍历展示部门信息, 拼接处理工委名
        for(ShowUser showUser : showUsers){
            //获取部门编码
            int userId = showUser.getUserId();
            List<String> menuAuthorityList = sysUserMapper.selectMenuAuthorityByUserId(userId);
            List<Integer> menuIds = sysUserMapper.getMenuIdsByUserId(userId);
            showUser.setMenuIds(menuIds);
            if (menuAuthorityList.size() != 0){
                menuAuthority = menuAuthorityList.toString();
                menuAuthority = menuAuthority.substring(menuAuthority.indexOf("[") + 1, menuAuthority.lastIndexOf("]"));
            }else {
                menuAuthority = "";
            }
            showUser.setMenuAuthority(menuAuthority);
        }
    }
}
