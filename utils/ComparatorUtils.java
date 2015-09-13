package me.wangolf.utils;

import java.util.Comparator;

import me.wangolf.bean.practice.PracDistriEntity;

public class ComparatorUtils implements Comparator<PracDistriEntity> {

	@Override
	public int compare(PracDistriEntity arg0, PracDistriEntity arg1) {
		// TODO Auto-generated method stub
		return  arg0.getPrice_type()-arg1.getPrice_type() ;
	}

}
