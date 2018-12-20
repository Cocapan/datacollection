package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.dao.*;
import cn.gz.rd.datacollection.model.*;
import cn.gz.rd.datacollection.utils.CountQuarterUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 汇总数据相关的服务
 */
@Service
public class JkwwPrjDataSummaryService {

    private final Logger logger = LoggerFactory.getLogger(JkwwPrjDataSummaryService.class);

    @Autowired
    private JkwwProjectScheduleSummaryMapper prjSummaryMapper;

    @Autowired
    private JkwwResidenceProblemScheduleMapper residenceMapper;

    @Autowired
    private JkwwYearlyPlanMapper yearlyPlanMapper;

    @Autowired
    private JkwwProjectScheduleSummaryMapper scheduleSummaryMapper;

    @Autowired
    private JkwwProjectPlanAndImplementMapper projectPlanAndImplementMapper;

    @Autowired
    private JkwwProblemScheduleMapper problemScheduleMapper;

    /**
     * 查询按分类汇总后的《“十三五”时期市本级社会民生基础设施建设项目进度总表》数据
     * @param countRate 统计周期
     * @return List<JkwwProjectScheduleSummary> 按分类汇总后的项目进度数据
     */
    public List<JkwwPrjPlanSummeryStr> queryPrjScheduleSummary(String countRate) {
        String previousQuarter = CountQuarterUtils.getPreviousQuarter(countRate);
        List<JkwwProjectScheduleSummary> currentQuarterPrjPlans =
                prjSummaryMapper.selectDeptData(null, countRate);
        List<JkwwProjectScheduleSummary> previousQuarterPrjPlans =
                prjSummaryMapper.selectDeptData(null, previousQuarter);

        Map<String, List<JkwwProjectScheduleSummary>> currentQuarterMap = toMapByType(currentQuarterPrjPlans);
        Map<String, List<JkwwProjectScheduleSummary>> previousQuarterMap = toMapByType(previousQuarterPrjPlans);

        List<JkwwPrjPlanSummary> prjPlanSummaries = new ArrayList<>();

        for (Map.Entry<String, List<JkwwProjectScheduleSummary>> listEntry : currentQuarterMap.entrySet()) {
            String type = listEntry.getKey();
            List<JkwwProjectScheduleSummary> currentQuarterList = listEntry.getValue();
            List<JkwwProjectScheduleSummary> previousQuarterList = previousQuarterMap.get(type);

            int zdsgzxfaNum = 0;
            List<String> zdsgzxfaNewAddPrjNames = new ArrayList<>();
            int zdqkNum = 0;
            List<String> zdqkNewAddPrjNames = new ArrayList<>();
            int qdjssgxkzqkNum = 0;
            List<String> qdjssgxkzqkNewAddPrjNames = new ArrayList<>();
            int ydzbqkNum = 0;
            List<String> ydzbqkNewAddPrjNames = new ArrayList<>();
            int yspfqkNum = 0;
            List<String> yspfqkNewAddPrjNames = new ArrayList<>();
            int ztbqkNum = 0;
            List<String> ztbqkNewAddPrjNames = new ArrayList<>();
            int djdwNum = 0;
            List<String> djdwNewAddPrjNames = new ArrayList<>();
            int tjjdqkNum = 0;
            List<String> tjjdqkNewAddPrjNames = new ArrayList<>();
            int nsgcqkNum = 0;
            List<String> nsgcqkNewAddPrjNames = new ArrayList<>();
            int ptgchjgcqkNum = 0;
            List<String> ptgchjgcqkNewAddPrjNames = new ArrayList<>();
            int ysyjsyqkNum = 0;
            List<String> ysyjsyqkNewAddPrjNames = new ArrayList<>();

            int size = currentQuarterList.size();
            for (int i = 0; i < size; i++) {

                JkwwProjectScheduleSummary currentPlan = currentQuarterList.get(i);
                JkwwProjectScheduleSummary previousPlan = previousQuarterList.get(i);

                String prjName = currentPlan.getXmmc();
                String currentSfzdsgzxfa = currentPlan.getLdqgzSfzdsgzxfa();
                String previousSfzdsgzxfa = previousPlan.getLdqgzSfzdsgzxfa();

                //完成个数
                if (StringUtils.equals("是", currentSfzdsgzxfa)) {
                    ++ zdsgzxfaNum;
                }

                //是否新增
                if (StringUtils.equals("是", currentSfzdsgzxfa) &&
                        !StringUtils.equals("是", previousSfzdsgzxfa)) {
                    zdsgzxfaNewAddPrjNames.add(prjName);
                }

                Double currentLdqgzZdqkwcl = currentPlan.getLdqgzZdqkwcl();
                Double previousLdqgzZdqkwcl = previousPlan.getLdqgzZdqkwcl();

                //完成个数
                if (currentLdqgzZdqkwcl != null && currentLdqgzZdqkwcl >= 100) {
                    ++ zdqkNum;
                }

                //是否新增
                if (currentLdqgzZdqkwcl != null && currentLdqgzZdqkwcl >= 100 &&
                        (previousLdqgzZdqkwcl == null || previousLdqgzZdqkwcl < 100)) {
                    zdqkNewAddPrjNames.add(prjName);
                }

                String ldqgzSfqdjssgxkz = currentPlan.getLdqgzSfqdjssgxkz();
                String ldqgzSfqdjssgxkz1 = previousPlan.getLdqgzSfqdjssgxkz();

                //完成个数
                if (StringUtils.equals("是", ldqgzSfqdjssgxkz)) {
                    ++ qdjssgxkzqkNum;
                }

                //是否新增
                if (StringUtils.equals("是", ldqgzSfqdjssgxkz) &&
                        !StringUtils.equals("是", ldqgzSfqdjssgxkz1)) {
                    qdjssgxkzqkNewAddPrjNames.add(prjName);
                }

                Double ldqgzYdzbqkwcl = currentPlan.getLdqgzYdzbqkwcl();
                Double ldqgzYdzbqkwcl1 = previousPlan.getLdqgzYdzbqkwcl();

                //完成个数
                if (ldqgzYdzbqkwcl != null && ldqgzYdzbqkwcl >= 100) {
                    ++ ydzbqkNum;
                }

                //是否新增
                if (ldqgzYdzbqkwcl != null && ldqgzYdzbqkwcl >= 100 &&
                        (ldqgzYdzbqkwcl1 == null || ldqgzYdzbqkwcl1 < 100)) {
                    ydzbqkNewAddPrjNames.add(prjName);
                }

                String ldqgzYssfpf = currentPlan.getLdqgzYssfpf();
                String ldqgzYssfpf1 = previousPlan.getLdqgzYssfpf();

                //完成个数
                if (StringUtils.equals("是", ldqgzYssfpf)) {
                    ++ yspfqkNum;
                }

                //是否新增
                if (StringUtils.equals("是", ldqgzYssfpf) &&
                        !StringUtils.equals("是", ldqgzYssfpf1)) {
                    yspfqkNewAddPrjNames.add(prjName);
                }

                String sgjcqkZtbsfwc = currentPlan.getSgjcqkZtbsfwc();
                String sgjcqkZtbsfwc1 = previousPlan.getSgjcqkZtbsfwc();

                //完成个数
                if (StringUtils.equals("已完成", sgjcqkZtbsfwc)) {
                    ++ ztbqkNum;
                }

                //是否新增
                if (StringUtils.equals("已完成", sgjcqkZtbsfwc) &&
                        !StringUtils.equals("已完成", sgjcqkZtbsfwc1)) {
                    ztbqkNewAddPrjNames.add(prjName);
                }

                String sgjcqkSfcydj = currentPlan.getSgjcqkSfcydj();//是否确定代建
                String sgjcqkSfcydj1 = previousPlan.getSgjcqkSfcydj();//是否确定代建

                //完成个数
                if (StringUtils.equals("是", sgjcqkSfcydj)) {
                    ++ djdwNum;
                }

                //是否新增
                if (StringUtils.equals("是", sgjcqkSfcydj) &&
                        !StringUtils.equals("是", sgjcqkSfcydj1)) {
                    djdwNewAddPrjNames.add(prjName);
                }

                Double sgjcqkTjjdbfl = currentPlan.getSgjcqkTjjdbfl();
                Double sgjcqkTjjdbfl1 = previousPlan.getSgjcqkTjjdbfl();

                //完成个数
                if (sgjcqkTjjdbfl != null && sgjcqkTjjdbfl >= 100) {
                    ++ tjjdqkNum;
                }

                //是否新增
                if (sgjcqkTjjdbfl != null && sgjcqkTjjdbfl >= 100 &&
                        (sgjcqkTjjdbfl1 == null || sgjcqkTjjdbfl1 < 100)) {
                    tjjdqkNewAddPrjNames.add(prjName);
                }

                Double sgjcqkNsgcjdbfl = currentPlan.getSgjcqkNsgcjdbfl();
                Double sgjcqkNsgcjdbfl1 = previousPlan.getSgjcqkNsgcjdbfl();

                //完成个数
                if (sgjcqkNsgcjdbfl != null && sgjcqkNsgcjdbfl >= 100) {
                    ++ nsgcqkNum;
                }

                //是否新增
                if (sgjcqkNsgcjdbfl != null && sgjcqkNsgcjdbfl >= 100 &&
                        (sgjcqkNsgcjdbfl1 == null || sgjcqkNsgcjdbfl1 < 100)) {
                    nsgcqkNewAddPrjNames.add(prjName);
                }

                Double ptgcHjgcjdbfl = currentPlan.getPtgcHjgcjdbfl();
                Double ptgcHjgcjdbfl1 = previousPlan.getPtgcHjgcjdbfl();

                //完成个数
                if (ptgcHjgcjdbfl != null && ptgcHjgcjdbfl >= 100) {
                    ++ ptgchjgcqkNum;
                }

                //是否新增
                if (ptgcHjgcjdbfl != null && ptgcHjgcjdbfl >= 100 &&
                        (ptgcHjgcjdbfl1 == null || ptgcHjgcjdbfl1 < 100)) {
                    ptgchjgcqkNewAddPrjNames.add(prjName);
                }

                String sfysbyjsy = currentPlan.getSfysbyjsy();
                String sfysbyjsy1 = previousPlan.getSfysbyjsy();

                //完成个数
                if (StringUtils.equals("是", sfysbyjsy)) {
                    ++ ysyjsyqkNum;
                }

                //是否新增
                if (StringUtils.equals("是", sfysbyjsy) &&
                        !StringUtils.equals("是", sfysbyjsy1)) {
                    ysyjsyqkNewAddPrjNames.add(prjName);
                }
            }

            JkwwPrjPlanSummary prjPlanSummary = new JkwwPrjPlanSummary();
            prjPlanSummary.setClassName(type);
            prjPlanSummary.setPrjCount(size);
            prjPlanSummary.setZdsgzxfaNum(zdsgzxfaNum);
            prjPlanSummary.setZdsgzxfaNewAddPrjNames(zdsgzxfaNewAddPrjNames);
            prjPlanSummary.setZdqkNum(zdqkNum);
            prjPlanSummary.setZdqkNewAddPrjNames(zdqkNewAddPrjNames);
            prjPlanSummary.setQdjssgxkzqkNum(qdjssgxkzqkNum);
            prjPlanSummary.setQdjssgxkzqkNewAddPrjNames(qdjssgxkzqkNewAddPrjNames);
            prjPlanSummary.setYdzbqkNum(ydzbqkNum);
            prjPlanSummary.setYdzbqkNewAddPrjNames(ydzbqkNewAddPrjNames);
            prjPlanSummary.setYspfqkNum(yspfqkNum);
            prjPlanSummary.setYspfqkNewAddPrjNames(yspfqkNewAddPrjNames);
            prjPlanSummary.setZtbqkNum(ztbqkNum);
            prjPlanSummary.setZtbqkNewAddPrjNames(ztbqkNewAddPrjNames);
            prjPlanSummary.setDjdwNum(djdwNum);
            prjPlanSummary.setDjdwNewAddPrjNames(djdwNewAddPrjNames);
            prjPlanSummary.setTjjdqkNum(tjjdqkNum);
            prjPlanSummary.setTjjdqkNewAddPrjNames(tjjdqkNewAddPrjNames);
            prjPlanSummary.setNsgcqkNum(nsgcqkNum);
            prjPlanSummary.setNsgcqkNewAddPrjNames(nsgcqkNewAddPrjNames);
            prjPlanSummary.setPtgchjgcqkNum(ptgchjgcqkNum);
            prjPlanSummary.setPtgchjgcqkNewAddPrjNames(ptgchjgcqkNewAddPrjNames);
            prjPlanSummary.setYsyjsyqkNum(ysyjsyqkNum);
            prjPlanSummary.setYsyjsyqkNewAddPrjNames(ysyjsyqkNewAddPrjNames);

            prjPlanSummaries.add(prjPlanSummary);
        }
        return toPrjPlanSummeryStr(prjPlanSummaries);
    }

    /**
     * 将解析好的数据转换成文本描述信息，用于填充：《“十三五”时期市本级社会民生基础设施建设项目进度总表》
     * @param prjPlanSummaries 解析的数据
     * @return List<JkwwPrjPlanSummeryStr> 代表表中的每一列
     */
    private List<JkwwPrjPlanSummeryStr> toPrjPlanSummeryStr(
            List<JkwwPrjPlanSummary> prjPlanSummaries) {
        List<JkwwPrjPlanSummeryStr> prjPlanSummeryStrs = new ArrayList<>();
        for (JkwwPrjPlanSummary prjPlanSummary : prjPlanSummaries) {
            JkwwPrjPlanSummeryStr prjPlanSummeryStr = new JkwwPrjPlanSummeryStr();
            int prjCount = prjPlanSummary.getPrjCount();
            prjPlanSummeryStr.setClassName(prjPlanSummary.getClassName());
            int zdsgzxfaNum = prjPlanSummary.getZdsgzxfaNum();
            List<String> zdsgzxfaNewAddPrjNames = prjPlanSummary.getZdsgzxfaNewAddPrjNames();
            int zdsgzxfaNewAddSize = zdsgzxfaNewAddPrjNames.size();
            prjPlanSummeryStr.setZdsgzxfaNum(zdsgzxfaNum);
            if (prjCount == zdsgzxfaNum) {
                prjPlanSummeryStr.setZdsgzxfa("全部完成");
            } else if (zdsgzxfaNewAddSize == 0) {
                prjPlanSummeryStr.setZdsgzxfa("0");
            } else if (zdsgzxfaNewAddSize > 0) {
                prjPlanSummeryStr.setZdsgzxfa("新增" + zdsgzxfaNewAddSize + "个项目："
                        + StringUtils.join(zdsgzxfaNewAddPrjNames, ","));
            }

            int zdqkNum = prjPlanSummary.getZdqkNum();
            List<String> zdqkNewAddPrjNames = prjPlanSummary.getZdqkNewAddPrjNames();
            int zdqkNewAddSize = zdqkNewAddPrjNames.size();
            prjPlanSummeryStr.setZdqkNum(zdqkNum);
            if (prjCount == zdqkNum) {
                prjPlanSummeryStr.setZdqk("全部完成");
            } else if (zdqkNewAddSize == 0) {
                prjPlanSummeryStr.setZdqk("0");
            } else if (zdqkNewAddSize > 0) {
                prjPlanSummeryStr.setZdqk("新增" + zdqkNewAddSize + "个项目："
                        + StringUtils.join(zdqkNewAddPrjNames, ","));
            }

            int qdjssgxkzqkNum = prjPlanSummary.getQdjssgxkzqkNum();
            List<String> qdjssgxkzqkNewAddPrjNames = prjPlanSummary.getQdjssgxkzqkNewAddPrjNames();
            int qdjssgxkzqkNewAddSize = qdjssgxkzqkNewAddPrjNames.size();
            prjPlanSummeryStr.setQdjssgxkzqkNum(qdjssgxkzqkNum);
            if (prjCount == qdjssgxkzqkNum) {
                prjPlanSummeryStr.setQdjssgxkzqk("全部完成");
            } else if (qdjssgxkzqkNewAddSize == 0) {
                prjPlanSummeryStr.setQdjssgxkzqk("0");
            } else if (qdjssgxkzqkNewAddSize > 0) {
                prjPlanSummeryStr.setQdjssgxkzqk("新增" + qdjssgxkzqkNewAddSize + "个项目："
                        + StringUtils.join(qdjssgxkzqkNewAddPrjNames, ","));
            }

            int ydzbqkNum = prjPlanSummary.getYdzbqkNum();
            List<String> ydzbqkNewAddPrjNames = prjPlanSummary.getYdzbqkNewAddPrjNames();
            int ydzbqkNewAddSize = ydzbqkNewAddPrjNames.size();
            prjPlanSummeryStr.setYdzbqkNum(ydzbqkNum);
            if (prjCount == ydzbqkNum) {
                prjPlanSummeryStr.setYdzbqk("全部完成");
            } else if (ydzbqkNewAddSize == 0) {
                prjPlanSummeryStr.setYdzbqk("0");
            } else if (ydzbqkNewAddSize > 0) {
                prjPlanSummeryStr.setYdzbqk("新增" + ydzbqkNewAddSize + "个项目："
                        + StringUtils.join(ydzbqkNewAddPrjNames, ","));
            }

            int yspfqkNum = prjPlanSummary.getYspfqkNum();
            List<String> yspfqkNewAddPrjNames = prjPlanSummary.getYspfqkNewAddPrjNames();
            int yspfqkNewAddSize = yspfqkNewAddPrjNames.size();
            prjPlanSummeryStr.setYspfqkNum(yspfqkNum);
            if (prjCount == yspfqkNum) {
                prjPlanSummeryStr.setYspfqk("全部完成");
            } else if (yspfqkNewAddSize == 0) {
                prjPlanSummeryStr.setYspfqk("0");
            } else if (yspfqkNewAddSize > 0) {
                prjPlanSummeryStr.setYspfqk("新增" + yspfqkNewAddSize + "个项目："
                        + StringUtils.join(yspfqkNewAddPrjNames, ","));
            }

            int ztbqkNum = prjPlanSummary.getZtbqkNum();
            List<String> ztbqkNewAddPrjNames = prjPlanSummary.getZtbqkNewAddPrjNames();
            int ztbqkNewAddSize = ztbqkNewAddPrjNames.size();
            prjPlanSummeryStr.setZtbqkNum(ztbqkNum);
            if (prjCount == ztbqkNum) {
                prjPlanSummeryStr.setZtbqk("全部完成");
            } else if (ztbqkNewAddSize == 0) {
                prjPlanSummeryStr.setZtbqk("0");
            } else if (ztbqkNewAddSize > 0) {
                prjPlanSummeryStr.setZtbqk("新增" + ztbqkNewAddSize + "个项目："
                        + StringUtils.join(ztbqkNewAddPrjNames, ","));
            }

            int djdwNum = prjPlanSummary.getDjdwNum();
            List<String> djdwNewAddPrjNames = prjPlanSummary.getDjdwNewAddPrjNames();
            int djdwNewAddSize = djdwNewAddPrjNames.size();
            prjPlanSummeryStr.setDjdwNum(djdwNum);
            if (prjCount == djdwNum) {
                prjPlanSummeryStr.setDjdw("全部完成");
            } else if (djdwNewAddSize == 0) {
                prjPlanSummeryStr.setDjdw("0");
            } else if (djdwNewAddSize > 0) {
                prjPlanSummeryStr.setDjdw("新增" + djdwNewAddSize + "个项目："
                        + StringUtils.join(djdwNewAddPrjNames, ","));
            }

            int tjjdqkNum = prjPlanSummary.getTjjdqkNum();
            List<String> tjjdqkNewAddPrjNames = prjPlanSummary.getTjjdqkNewAddPrjNames();
            int tjjdqkNewAddSize = tjjdqkNewAddPrjNames.size();
            prjPlanSummeryStr.setTjjdqkNum(tjjdqkNum);
            if (prjCount == tjjdqkNum) {
                prjPlanSummeryStr.setTjjdqk("全部完成");
            } else if (tjjdqkNewAddSize == 0) {
                prjPlanSummeryStr.setTjjdqk("0");
            } else if (tjjdqkNewAddSize > 0) {
                prjPlanSummeryStr.setTjjdqk("新增" + tjjdqkNewAddSize + "个项目："
                        + StringUtils.join(tjjdqkNewAddPrjNames, ","));
            }

            int nsgcqkNum = prjPlanSummary.getNsgcqkNum();
            List<String> nsgcqkNewAddPrjNames = prjPlanSummary.getNsgcqkNewAddPrjNames();
            int nsgcqkNewAddSize = nsgcqkNewAddPrjNames.size();
            prjPlanSummeryStr.setNsgcqkNum(nsgcqkNum);
            if (prjCount == nsgcqkNum) {
                prjPlanSummeryStr.setNsgcqk("全部完成");
            } else if (nsgcqkNewAddSize == 0) {
                prjPlanSummeryStr.setNsgcqk("0");
            } else if (nsgcqkNewAddSize > 0) {
                prjPlanSummeryStr.setNsgcqk("新增" + nsgcqkNewAddSize + "个项目："
                        + StringUtils.join(nsgcqkNewAddPrjNames, ","));
            }

            int ptgchjgcqkNum = prjPlanSummary.getPtgchjgcqkNum();
            List<String> ptgchjgcqkNewAddPrjNames = prjPlanSummary.getPtgchjgcqkNewAddPrjNames();
            int ptgchjgcqkNewAddSize = ptgchjgcqkNewAddPrjNames.size();
            prjPlanSummeryStr.setPtgchjgcqkNum(ptgchjgcqkNum);
            if (prjCount == ptgchjgcqkNum) {
                prjPlanSummeryStr.setPtgchjgcqk("全部完成");
            } else if (ptgchjgcqkNewAddSize == 0) {
                prjPlanSummeryStr.setPtgchjgcqk("0");
            } else if (ptgchjgcqkNewAddSize > 0) {
                prjPlanSummeryStr.setPtgchjgcqk("新增" + ptgchjgcqkNewAddSize + "个项目："
                        + StringUtils.join(ptgchjgcqkNewAddPrjNames, ","));
            }

            int ysyjsyqkNum = prjPlanSummary.getYsyjsyqkNum();
            List<String> ysyjsyqkNewAddPrjNames = prjPlanSummary.getYsyjsyqkNewAddPrjNames();
            int ysyjsyqkNewAddSize = ysyjsyqkNewAddPrjNames.size();
            prjPlanSummeryStr.setYsyjsyqkNum(ysyjsyqkNum);
            if (prjCount == ysyjsyqkNum) {
                prjPlanSummeryStr.setYsyjsyqk("全部完成");
            } else if (ysyjsyqkNewAddSize == 0) {
                prjPlanSummeryStr.setYsyjsyqk("0");
            } else if (ysyjsyqkNewAddSize > 0) {
                prjPlanSummeryStr.setYsyjsyqk("新增" + ysyjsyqkNewAddSize + "个项目："
                        + StringUtils.join(ysyjsyqkNewAddPrjNames, ","));
            }
            prjPlanSummeryStrs.add(prjPlanSummeryStr);
        }
        return prjPlanSummeryStrs;
    }

    private Map<String, List<JkwwProjectScheduleSummary>>
        toMapByType(List<JkwwProjectScheduleSummary> prjPlans) {
        Map<String, List<JkwwProjectScheduleSummary>> quarterMap = new HashMap<>();
        for (JkwwProjectScheduleSummary prjSchedule : prjPlans) {
            String fl = prjSchedule.getFl();
            if (quarterMap.containsKey(fl)) {
                List<JkwwProjectScheduleSummary> summaries = quarterMap.get(fl);
                summaries.add(prjSchedule);
            } else {
                List<JkwwProjectScheduleSummary> summaries = new ArrayList<>();
                summaries.add(prjSchedule);
                quarterMap.put(fl, summaries);
            }
        }
        return  quarterMap;
    }

    /**
     * 查询重点项目的《“十三五”时期市本级社会民生基础设施建设项目进度总表》数据
     * @param countRate 统计周期
     * @return List<JkwwProjectScheduleSummary> 重点项目进度数据
     */
    public List<JkwwProjectScheduleSummary> queryKeyPrjScheduleSummary(String countRate) {
        return prjSummaryMapper.selectKeyPrjScheduleSummary(countRate);
    }

    /**
     * 查询《居住区配套设施建设移交历史遗留问题台账》数据
     * @param deptName 部门名称
     * @param countRate 统计周期
     * @return List<JkwwResidenceLedger>
     */
    public List<JkwwResidenceLedger> queryResidenceLedger(String deptName, String countRate) {
        return residenceMapper.selectResidenceLedger(deptName, countRate);
    }

    /**
     * 根据条件查询《年度计划表》
     */
    public List<JkwwYearlyPlan> queryYearlyPlanByCondition(
            String className, String status, String mainDeptName, String ownerName,
            String projectName, String countRate, Integer startRow, Integer pageSize) {

        if (StringUtils.isBlank(className)) {
            className = null;
        }
        if (StringUtils.isBlank(status)) {
            status = null;
        }
        if (StringUtils.isBlank(mainDeptName)) {
            mainDeptName = null;
        } else {
            mainDeptName = "%" + mainDeptName + "%";
        }
        if (StringUtils.isBlank(ownerName)) {
            ownerName = null;
        } else {
            ownerName = "%" + ownerName + "%";
        }
        if (StringUtils.isBlank(projectName)) {
            projectName = null;
        } else {
            projectName = "%" + projectName + "%";
        }
        if (StringUtils.isBlank(countRate)) {
            countRate = null;
        }

        List<JkwwYearlyPlan> jkwwYearlyPlans = yearlyPlanMapper.selectYearlyPlan(
                className, status, mainDeptName, ownerName,
                projectName, countRate, startRow, pageSize);
        if (logger.isDebugEnabled()) {
            logger.debug("查询汇总的《年度计划表》数据，请求参数：className = {}, status = {}, mainDeptName = {}, " +
                            "ownerName = {}, projectName = {}, countRate = {}, startRow = {}, pageSize = {}",
                    className, status, mainDeptName, ownerName, projectName, countRate, startRow, pageSize);
            logger.info("查询汇总的《年度计划表》数据，总条数 = {}", jkwwYearlyPlans.size());
        }
        return jkwwYearlyPlans;
    }

    /**
     * 根据条件查询《年度计划表》数据总条数
     */
    public int countYearlyPlanByCondition(
            String className, String status, String mainDeptName, String ownerName,
            String projectName, String countRate) {

        if (StringUtils.isBlank(className)) {
            className = null;
        }
        if (StringUtils.isBlank(status)) {
            status = null;
        }
        if (StringUtils.isBlank(mainDeptName)) {
            mainDeptName = null;
        } else {
            mainDeptName = "%" + mainDeptName + "%";
        }
        if (StringUtils.isBlank(ownerName)) {
            ownerName = null;
        } else {
            ownerName = "%" + ownerName + "%";
        }
        if (StringUtils.isBlank(projectName)) {
            projectName = null;
        } else {
            projectName = "%" + projectName + "%";
        }
        if (StringUtils.isBlank(countRate)) {
            countRate = null;
        }

        int count = yearlyPlanMapper.countYearlyPlan(
                className, status, mainDeptName,
                ownerName, projectName, countRate);
        if (logger.isDebugEnabled()) {
            logger.debug("查询汇总的《年度计划表》数据总条数，请求参数：className = {}, status = {}, mainDeptName = {}, " +
                            "ownerName = {}, projectName = {}, countRate = {}",
                    className, status, mainDeptName, ownerName, projectName, countRate);
            logger.info("查询汇总的《年度计划表》数据总条数，总条数 = {}", count);
        }
        return count;
    }

    /**
     * 根据条件查询《进度总表》
     */
    public List<JkwwProjectScheduleSummary> queryScheduleSummaryByCondition(
            String className, String status, String mainDeptName, String ownerName,
            String projectName, String countRate, Boolean isKeyPrj,Integer startRow,
            Integer pageSize) {

        if (StringUtils.isBlank(className)) {
            className = null;
        }
        if (StringUtils.isBlank(status)) {
            status = null;
        }
        if (StringUtils.isBlank(mainDeptName)) {
            mainDeptName = null;
        } else {
            mainDeptName = "%" + mainDeptName + "%";
        }
        if (StringUtils.isBlank(ownerName)) {
            ownerName = null;
        } else {
            ownerName = "%" + ownerName + "%";
        }
        if (StringUtils.isBlank(projectName)) {
            projectName = null;
        } else {
            projectName = "%" + projectName + "%";
        }
        if (StringUtils.isBlank(countRate)) {
            countRate = null;
        }

        List<JkwwProjectScheduleSummary> jkwwYearlyPlans = scheduleSummaryMapper.selectScheduleSummary(
                className, status, mainDeptName, ownerName,
                projectName, countRate, isKeyPrj, startRow, pageSize);
        if (logger.isDebugEnabled()) {
            logger.debug("查询汇总的《进度总表》数据，请求参数：className = {}, status = {}, mainDeptName = {}, " +
                            "ownerName = {}, projectName = {}, countRate = {}, isKeyPrj = {}, startRow = {}, pageSize = {}",
                    className, status, mainDeptName, ownerName, projectName, countRate, isKeyPrj,startRow, pageSize);
            logger.info("查询汇总的《进度总表》数据，总条数 = {}", jkwwYearlyPlans.size());
        }
        return jkwwYearlyPlans;
    }

    /**
     * 根据条件查询《进度总表》数据总条数
     */
    public int countScheduleSummaryByCondition(
            String className, String status, String mainDeptName, String ownerName,
            String projectName, String countRate, Boolean isKeyPrj) {

        if (StringUtils.isBlank(className)) {
            className = null;
        }
        if (StringUtils.isBlank(status)) {
            status = null;
        }
        if (StringUtils.isBlank(mainDeptName)) {
            mainDeptName = null;
        } else {
            mainDeptName = "%" + mainDeptName + "%";
        }
        if (StringUtils.isBlank(ownerName)) {
            ownerName = null;
        } else {
            ownerName = "%" + ownerName + "%";
        }
        if (StringUtils.isBlank(projectName)) {
            projectName = null;
        } else {
            projectName = "%" + projectName + "%";
        }
        if (StringUtils.isBlank(countRate)) {
            countRate = null;
        }

        int count = scheduleSummaryMapper.countScheduleSummary(
                className, status, mainDeptName,
                ownerName, projectName, countRate, isKeyPrj);
        if (logger.isDebugEnabled()) {
            logger.debug("查询汇总的《进度总表》数据总条数，请求参数：className = {}, status = {}, mainDeptName = {}, " +
                            "ownerName = {}, projectName = {}, countRate = {}, isKeyPrj = {}",
                    className, status, mainDeptName, ownerName, projectName, countRate, isKeyPrj);
            logger.info("查询汇总的《进度总表》数据总条数，总条数 = {}", count);
        }
        return count;
    }

    /**
     * 查询汇总的《民生基础设施布局规划及其实施方案制定实施情况表》
     */
    public List<JkwwProjectPlanAndImplement> queryPrjPlanAndImpl(String countRate) {
        List<JkwwProjectPlanAndImplement> planAndImplements =
                projectPlanAndImplementMapper.selectDeptData(null, countRate);
        return planAndImplements;
    }

    /**
     * 查询汇总的《解决历史遗留问题进度表》
     */
    public List<JkwwProblemSchedule> queryHistoryProblemSchedule(String countRate) {
        return problemScheduleMapper.selectDeptData(null, countRate);
    }

}
