package com.meiren.utils;

import com.alibaba.fastjson.JSON;
import com.meiren.mission.enums.*;
import com.meiren.mission.service.vo.ActionVO;
import com.meiren.mission.service.vo.FilterRuleVO;
import com.meiren.mission.service.vo.FilterRuleDetail;
import com.meiren.mission.service.vo.MissionConditionVO;
import org.apache.commons.collections.map.HashedMap;

import java.util.*;

public  class MissionUtils {
	/**
	 * 根据任务的类型获取对应的奖励规则
	 *
	 * @param filterRuleVOList
	 * @param identityTypeEnum
	 * @return
	 */
	public static  FilterRuleVO getFilterRuleVOByIdentityType(List<FilterRuleVO> filterRuleVOList,
			IdentityTypeEnum identityTypeEnum) {
		if (identityTypeEnum == null) {
			for (FilterRuleVO filterRuleVO : filterRuleVOList) {
				if (filterRuleVO.getRuleTypeEnum().equals(RuleTypeEnum.SHAREAWARD)) {
					return filterRuleVO;
				}
			}
		} else if (identityTypeEnum.equals(IdentityTypeEnum.SPONSOR)) {
			for (FilterRuleVO filterRuleVO : filterRuleVOList) {
				if (filterRuleVO.getRuleTypeEnum().equals(RuleTypeEnum.SPONSORAWARD)) {
					return filterRuleVO;
				}
			}

		} else if (identityTypeEnum.equals(IdentityTypeEnum.PARTICIPATOR)) {
			for (FilterRuleVO filterRuleVO : filterRuleVOList) {
				if (filterRuleVO.getRuleTypeEnum().equals(RuleTypeEnum.PARTICIPATORAWARD)) {
					return filterRuleVO;
				}
			}
		}
		return null;
	}
	/**
	 * 返回奖励数值
	 *
	 * @param filterRuleVO
	 * @return
	 */
	public static HashMap awardMap(FilterRuleVO filterRuleVO){
		HashMap awardMap = new HashMap();
		List<ActionVO> actionVOList = filterRuleVO.getActionVOList();
/*		for (ActionVO actionVO : actionVOList){
			if(actionVO.getAwardType()==AwardTypeEnum.COUPON.typeValue){
				awardMap.put(AwardTypeEnum.COUPON, actionVO.getAmount());
			}else if(actionVO.getAwardType()==AwardTypeEnum.SCORE.typeValue){
				awardMap.put(AwardTypeEnum.SCORE, actionVO.getAmount());
			}else {
				awardMap.put(AwardTypeEnum.CASH, actionVO.getAmount());
			}
		}*/
		return awardMap;


	}
	/**
	 * 返回奖励数值
	 *
	 * @param filterRuleVOList
	 * @return
	 */
	public static HashMap awardMapByIdentityTypeEnum(List<FilterRuleVO> filterRuleVOList,RuleTypeEnum ruleTypeEnum) {
		HashMap awardMap = new HashMap();
		for (FilterRuleVO filterRuleVO : filterRuleVOList
				) {
			if (filterRuleVO.getRuleTypeEnum().equals(ruleTypeEnum)) {
				awardMap = awardMap(filterRuleVO);

				return awardMap;
			}
		}
		return awardMap;

	}

	/**
	 * 给奖励赋值
	 *
	 * @param
	 * @return
	 */
	public static List<FilterRuleVO> setAwardAmount(List<FilterRuleVO> filterRuleVOList,IdentityTypeEnum identityTypeEnum,Integer[] amount){
		if(filterRuleVOList == null){
			//String ruleContent ="[{'ruleTypeEnum':'SPONSOR','filterRuleDetailList':[{'ruleKey':null,'ruleValue':null,'operationEnum':null},{'ruleKey':null,'ruleValue':null,'operationEnum':null}],'actionVOList':[]},{'ruleTypeEnum':'PARTICIPATOR','filterRuleDetailList':[{'ruleKey':null,'ruleValue':null,'operationEnum':null},{'ruleKey':null,'ruleValue':null,'operationEnum':null}],'actionVOList':[]},{'ruleTypeEnum':'SPONSORAWARD','filterRuleDetailList':[{'ruleKey':null,'ruleValue':null,'operationEnum':null},{'ruleKey':null,'ruleValue':null,'operationEnum':null}],'actionVOList':[{'awardType':null,'amount':null,'couponCode':null},{'awardType':null,'amount':null,'couponCode':null}],'isAuto':null,'formId':null},{'ruleTypeEnum':'PARTICIPATORAWARD','filterRuleDetailList':[{'ruleKey':null,'ruleValue':null,'operationEnum':null}],'actionVOList':[{'awardType':null,'amount':null,'couponCode':null},{'awardType':null,'amount':null,'couponCode':null}],'isAuto':null,'formId':null},{'ruleTypeEnum':'SHAREAWARD','filterRuleDetailList':[],'actionVOList':[{'awardType':'2','amount':null,'couponCode':null},{'awardType':'3','amount':null,'couponCode':null}]}]";
			String ruleContent ="[{'actionVOList':[],'filterRuleDetailList':[],'ruleTypeEnum':'SPONSOR'},{'actionVOList':[],'filterRuleDetailList':[],'ruleTypeEnum':'PARTICIPATOR'},{'actionVOList':[],'filterRuleDetailList':[],'ruleTypeEnum':'SPONSORAWARD'},{'actionVOList':[],'filterRuleDetailList':[{'ruleKey':null,'ruleValue':null,'operationEnum':null}],'ruleTypeEnum':'PARTICIPATORAWARD'},{'actionVOList':[{'awardType':'2','amount':null,'couponCode':null},{'awardType':'3','amount':null,'couponCode':null}],'filterRuleDetailList':[],'ruleTypeEnum':'SHAREAWARD'}]";
			filterRuleVOList = JSON.parseArray(ruleContent, FilterRuleVO.class);
		}
		HashMap awardMap = new HashMap();

		FilterRuleVO filterRuleVO = getFilterRuleVOByIdentityType(filterRuleVOList, identityTypeEnum);
		List<ActionVO> actionVOList = filterRuleVO.getActionVOList();
		for (ActionVO actionVO : actionVOList){
			if(actionVO.getAwardType()==AwardTypeEnum.COUPON.typeValue){
				actionVO.setAmount(null);
			}else if(actionVO.getAwardType()==AwardTypeEnum.SCORE.typeValue){
				actionVO.setAmount(amount[0]);
			}else {
				actionVO.setAmount(amount[1]);
			}
		}
		return filterRuleVOList;


	}

	/**
	 * 给条件赋值
	 *
	 * @param
	 * @return
	 */
	public static String setRule(String ruleContent,FilterRuleDetail filterRuleDetail ,RuleTypeEnum ruleTypeEnum,boolean isAuto){
		List<FilterRuleVO> filterRuleVOList = JSON.parseArray(ruleContent, FilterRuleVO.class);


		for (FilterRuleVO filterRuleVO:filterRuleVOList
			 ) {
			if(filterRuleVO.getRuleTypeEnum().equals(ruleTypeEnum)){
				List<FilterRuleDetail> filterRuleDetailList = filterRuleVO.getFilterRuleDetailList();
				for (FilterRuleDetail filterRuleDetail1Original:filterRuleDetailList
					 ) {
					if(filterRuleDetail1Original.getRuleKey()!=null&&filterRuleDetail1Original.getRuleKey().equals(filterRuleDetail.getRuleKey())){
						filterRuleDetail1Original.setRuleValue(filterRuleDetail.getRuleValue());
						filterRuleDetail1Original.setOperationEnum(filterRuleDetail.getOperationEnum());

						filterRuleVO.setIsAuto(isAuto);
						ruleContent = JSON.toJSONString(filterRuleVOList);

						return ruleContent;
					}
				}
				filterRuleVO.setIsAuto(isAuto);
				filterRuleDetailList.add(filterRuleDetail);
				}
			}
		ruleContent = JSON.toJSONString(filterRuleVOList);
		return ruleContent;

	}

	public static Boolean booleanGenerator(){
		Random r=new Random(); 
		return r.nextBoolean();
	}

	//``````````````````````````````````````````````````````````````````````````````````````````````````````````````
	//``````````````````````````````````````````````````````````````````````````````````````````````````````````````
	//``````````````````````````````````````````````````````````````````````````````````````````````````````````````
	//``````````````````````````````````````````````````````````````````````````````````````````````````````````````
	//``````````````````````````````````````````````````````````````````````````````````````````````````````````````
	//``````````````````````````````````````````````````````````````````````````````````````````````````````````````


	/**
	 * 创建一个新的规则
	 * 要求5个ruleTypeNum都得有
	 */

	public static List<FilterRuleVO> newFilterRuleVOList(){
		List<FilterRuleVO> filterRuleVOList = new ArrayList<>();
		FilterRuleVO filterRuleVOSPONSOR = new FilterRuleVO();
		FilterRuleVO filterRuleVOPARTICIPATOR = new FilterRuleVO();
		FilterRuleVO filterRuleVOSPONSORAWARD = new FilterRuleVO();
		FilterRuleVO filterRuleVOPARTICIPATORAWARD = new FilterRuleVO();
		FilterRuleVO filterRuleVOSHAREAWARD = new FilterRuleVO();
		List<ActionVO> actionVOList = new ArrayList<>();
		List<FilterRuleDetail> filterRuleDetailList = new ArrayList<>();
		filterRuleVOSPONSOR.setRuleTypeEnum(RuleTypeEnum.SPONSOR);
		filterRuleVOSPONSOR.setActionVOList(actionVOList);
		filterRuleVOSPONSOR.setFilterRuleDetailList(filterRuleDetailList);

		filterRuleVOPARTICIPATOR.setRuleTypeEnum(RuleTypeEnum.PARTICIPATOR);
		filterRuleVOPARTICIPATOR.setActionVOList(actionVOList);
		filterRuleVOPARTICIPATOR.setFilterRuleDetailList(filterRuleDetailList);

		filterRuleVOSPONSORAWARD.setRuleTypeEnum(RuleTypeEnum.SPONSORAWARD);
		filterRuleVOSPONSORAWARD.setActionVOList(actionVOList);
		filterRuleVOSPONSORAWARD.setFilterRuleDetailList(filterRuleDetailList);

		filterRuleVOPARTICIPATORAWARD.setRuleTypeEnum(RuleTypeEnum.PARTICIPATORAWARD);
		filterRuleVOPARTICIPATORAWARD.setActionVOList(actionVOList);
		filterRuleVOPARTICIPATORAWARD.setFilterRuleDetailList(filterRuleDetailList);

		filterRuleVOSHAREAWARD.setRuleTypeEnum(RuleTypeEnum.SHAREAWARD);
		filterRuleVOSHAREAWARD.setActionVOList(actionVOList);
		filterRuleVOSHAREAWARD.setFilterRuleDetailList(filterRuleDetailList);

		filterRuleVOList.add(filterRuleVOSPONSOR);
		filterRuleVOList.add(filterRuleVOPARTICIPATOR);
		filterRuleVOList.add(filterRuleVOSPONSORAWARD);
		filterRuleVOList.add(filterRuleVOPARTICIPATORAWARD);
		filterRuleVOList.add(filterRuleVOSHAREAWARD);

		return filterRuleVOList;
	}
	/**
	 * 根据任务类型判断奖励给谁
	 */
	public static RuleTypeEnum getAwardRuleTypeEnumByMissionType(MissionTypeEnum missionTypeEnum){
		//拉新任务奖励数额

			switch (missionTypeEnum){
			case NEWER:
				return RuleTypeEnum.SHAREAWARD;
			case FORM:
				return RuleTypeEnum.SHAREAWARD;
			case DISCOVER:
				return RuleTypeEnum.SHAREAWARD;
			case WELFARE:
				return RuleTypeEnum.SHAREAWARD;
			case VIDEO:
				return RuleTypeEnum.SHAREAWARD;
			case EXPAND:
				return RuleTypeEnum.PARTICIPATORAWARD;
			default:
				return RuleTypeEnum.SHAREAWARD;
			}

	}
	/**
	 * 根据任务类型判断奖励发放条件给谁
	 */
	public static RuleTypeEnum getRuleRuleTypeEnumByMissionType(MissionTypeEnum missionTypeEnum){
		//拉新任务奖励发放规则
		switch (missionTypeEnum){
		case NEWER:
			return RuleTypeEnum.PARTICIPATORAWARD;
		case FORM:
			return RuleTypeEnum.PARTICIPATORAWARD;
		case DISCOVER:
			return RuleTypeEnum.PARTICIPATORAWARD;
		case WELFARE:
			return RuleTypeEnum.PARTICIPATORAWARD;
		case VIDEO:
			return RuleTypeEnum.PARTICIPATORAWARD;
		case EXPAND:
			return RuleTypeEnum.PARTICIPATORAWARD;
		default:
			return RuleTypeEnum.PARTICIPATORAWARD;
		}

	}

	/**
	 * 任务创建，奖励赋值
	 *
	 * @param
	 * @return
	 */
	public static List<FilterRuleVO> setAwardAmount2(List<FilterRuleVO> filterRuleVOList, MissionTypeEnum missionTypeEnum, Map<String,String> awardMap){
		RuleTypeEnum ruleTypeEnum = getAwardRuleTypeEnumByMissionType(missionTypeEnum);
		//创建规则列表
		if(filterRuleVOList==null){
			filterRuleVOList = newFilterRuleVOList();
			for (FilterRuleVO filterRuleVO:filterRuleVOList
				 ) {
				if(filterRuleVO.getRuleTypeEnum()==ruleTypeEnum){
					filterRuleVO.setActionVOList(setActionVOList(awardMap));
				}
			}
			return filterRuleVOList;

		}else{
			for (FilterRuleVO filterRuleVO:filterRuleVOList
				 ) {
				if(filterRuleVO.getRuleTypeEnum()==ruleTypeEnum)
					filterRuleVO.setActionVOList(setActionVOList(awardMap));

			}
			return filterRuleVOList;
		}


	}
	/**
	 * 奖励赋值
	 *
	 * @param
	 * @return
	 */
	public static List<ActionVO> setActionVOList(Map<String,String> awardMap){
		List<ActionVO> actionVOList = new ArrayList<>();
		Iterator<Map.Entry<String,String>> iterator = awardMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String,String> entry = iterator.next();
			ActionVO actionVO = new ActionVO();
			if(entry.getValue()!=null&&!entry.getValue().equals("")){
				actionVO.setAmount(Integer.valueOf(entry.getValue()));
				actionVO.setAwardType(AwardTypeEnum.getTypeValueByName(entry.getKey()));
				actionVOList.add(actionVO);
			}

		}
		return actionVOList;
	}



	/**
	 *任务创建,设置规则
	 */
	public static String setFilterRule(String ruleContent,List<MissionConditionVO> missionConditionVOList,boolean isAuto,MissionTypeEnum missionTypeEnum){
		List<FilterRuleVO> filterRuleVOList =  JSON.parseArray(ruleContent, FilterRuleVO.class);
		RuleTypeEnum ruleTypeEnum = getRuleRuleTypeEnumByMissionType(missionTypeEnum);
		for (FilterRuleVO filterRuleVO:filterRuleVOList
			 ) {
			if(filterRuleVO.getRuleTypeEnum() == ruleTypeEnum){

					filterRuleVO.setFilterRuleDetailList(setFilterRuleDetailList(missionConditionVOList));
					filterRuleVO.setIsAuto(isAuto);


			}
		}
		ruleContent = JSON.toJSONString(filterRuleVOList);
		return ruleContent;
	}
	/**
	 * 设置规则
	 *
	 * @param
	 * @return
	 */
	public static List<FilterRuleDetail> setFilterRuleDetailList(List<MissionConditionVO> missionConditionVOList){
		List<FilterRuleDetail> filterRuleDetailList = new ArrayList<>();
		for (MissionConditionVO missionConditionVO:missionConditionVOList
			 ) {
			if(missionConditionVO.getNumber() != null&&!missionConditionVO.getNumber().equals("")){
				FilterRuleDetail filterRuleDetail = new FilterRuleDetail();
				filterRuleDetail.setRuleKey(missionConditionVO.getMissionConditionEnum().name);
				filterRuleDetail.setRuleValue(missionConditionVO.getNumber().toString());
				filterRuleDetail.setOperationEnum(missionConditionVO.getOperationEnum());
				filterRuleDetailList.add(filterRuleDetail);
			}
		}
		return filterRuleDetailList;
	}
	/**
	 * 根据任务的类型获取对应的奖励
	 *
	 * @param filterRuleVOList
	 * @param missionTypeEnum
	 * @return
	 */
	public static HashMap getAwardMap(List<FilterRuleVO> filterRuleVOList,MissionTypeEnum missionTypeEnum){
		HashMap awardMap = new HashMap();
		RuleTypeEnum ruleTypeEnum = getAwardRuleTypeEnumByMissionType(missionTypeEnum);
		for (FilterRuleVO filterRuleVO:filterRuleVOList
			 ) {
			if(filterRuleVO.getRuleTypeEnum() == ruleTypeEnum){
				List<ActionVO> actionVOList = filterRuleVO.getActionVOList();
				for (ActionVO actionVO : actionVOList){
					if(actionVO.getAwardType()==AwardTypeEnum.COUPON.typeValue){
						awardMap.put(AwardTypeEnum.COUPON, actionVO.getAmount());
					}else if(actionVO.getAwardType()==AwardTypeEnum.SCORE.typeValue){
						awardMap.put(AwardTypeEnum.SCORE, actionVO.getAmount());
					}else {
						awardMap.put(AwardTypeEnum.CASH, actionVO.getAmount());
					}
				}

			}
		}

		return awardMap;


	}

	/**
	 * 根据任务的类型获取对应的规则
	 *
	 * @param filterRuleVOList
	 * @param missionTypeEnum
	 * @return
	 */
	public static  FilterRuleVO getFilterRuleVOByMissionType(List<FilterRuleVO> filterRuleVOList,MissionTypeEnum missionTypeEnum){
		RuleTypeEnum ruleTypeEnum = getRuleRuleTypeEnumByMissionType(missionTypeEnum);
		for (FilterRuleVO filterRuleVO:
		filterRuleVOList) {
			if(filterRuleVO.getRuleTypeEnum()==ruleTypeEnum){
				return filterRuleVO;
			}
		}
		return null;
	}

}
