package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.Department;
import cn.gz.rd.datacollection.model.ShowDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author panxuan
 * 部门数据访问层
 */
@Mapper
@Component
public interface DepartmentMapper {

    /**
     * 通过工委编码模糊匹配部门名, 获取部门名列表
     * @param workingCommitteeId 工委编码
     * @param departmentName 部门名
     * @return 部门名列表
     */
    List<String> selectDepartmentNamesByWorkCommitteeIdLikeDepartmentName(@Param("workingCommitteeId") String workingCommitteeId, @Param("departmentName") String departmentName);

    /**
     * 通过部门编码, 获取部门名
     * @param departmentId 部门编码
     * @return 部门名
     */
    String selectDepartmentNameByDepartmentId(String departmentId);

    /**
     * 通过部门编码, 获取部门名列表
     * @param departmentId 部门编码
     * @return 部门名列表
     */
    List<String> selectDepartmentNamesByDepartmentId(String departmentId);

    /**
     * 通过模糊匹配部门名, 获取部门名列表
     * @param departmentName 部门名
     * @return 部门名列表
     */
    List<String> selectDepartmentNamesLikeDepartmentName(@Param("departmentName") String departmentName);

    /**
     * 通过模糊匹配部门编码, 获取展示部门信息列表
     * @param departmentId 部门编码
     * @return 展示部门信息列表
     */
    List<String> selectLikeDepartmentId(@Param("departmentId") String departmentId);

    /**
     * 通过工委编码及模糊匹配部门编码, 获取展示部门信息列表
     * @param workingCommitteeId 工委编码
     * @param departmentId 部门编码
     * @return 展示部门信息列表
     */
    List<String> selectByWorkingCommitteeIdLikeDepartmentId(@Param("workingCommitteeId") String workingCommitteeId, @Param("departmentId") String departmentId);

    /**
     * 获取包含工委名的展示部门信息列表
     * @return 包含工委名的展示部门信息列表
     */
    List<ShowDepartment> pageQueryAll(
            @Param(value = "startSize") int startSize,
            @Param(value = "pageSize") int pageSize);

    int countAll();

    List<String> selectWorkingCommitteeNameByDepartmentId(String departmentId);

    /**
     * 通过工委编码, 获取包含工委名的展示部门信息列表
     * @param workingCommitteeId 工委编码
     * @return 包含工委名的展示部门信息列表
     */
    List<ShowDepartment> pageQueryAllByWorkingCommitteeId(@Param(value = "workingCommitteeId")String workingCommitteeId,
                                                          @Param(value = "startSize") int startSize,
                                                          @Param(value = "pageSize") int pageSize);

    int countAllByWorkingCommitteeId(String workingCommitteeId);

    /**
     * 通过部门编码及部门名, 获取展示部门信息列表
     * @param departmentId 部门编码
     * @param departmentName 部门名
     * @return 展示部门信息列表
     */
    List<ShowDepartment> pageQueryByCondition(@Param("departmentId") String departmentId,
                                              @Param("departmentName") String departmentName,
                                              @Param(value = "startSize") int startSize,
                                              @Param(value = "pageSize") int pageSize);

    int countAllByCondition(@Param("departmentId") String departmentId,
                            @Param("departmentName") String departmentName);

    /**
     * 插入部门信息
     * @param department 部门信息
     * @return 1 插入成功, 0 插入失败
     */
    int insert(Department department);

    /**
     * 获取最新的部门编码
     * @return 最新的部门编码
     */
    String selectLastDepartmentId();

    /**
     * 更新部门信息
     * @param department 部门信息
     * @return 1 更新成功, 0 更新失败
     */
    int updateByPrimaryKey(Department department);

    /**
     * 通过部门名, 获取部门信息
     * @param departmentName 部门名
     * @return 部门信息
     */
    Department selectByDepartmentName(String departmentName);

    /**
     * 获取工委名列表
     * @return 工委名列表
     */
    List<Department> selectDepartmentsByWorkingCommitteeId(String workingCommitteeId);

    List<String> getWorkingCommitteeIdsByDeptId(String deptId);

    /**
     * 获取工委名列表
     * @return 工委名列表
     */
    List<Department> selectDepartments();

    List<Department> selectDepartmentsByWorkingCommitteeIdAndUploadPeriod(@Param(value = "workingCommitteeId") String workingCommitteeId, @Param(value = "uploadPeriod") String uploadPeriod);

}
