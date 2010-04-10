/***************************************************************************
 *   Copyright (C) 2010 by                                                 *
 *   	Matej Jakop <matej@jakop.si>                                       *
 *      Gregor Kališnik <gregor@unimatrix-one.org>                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License version 3        *
 *   as published by the Free Software Foundation.                         *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 ***************************************************************************/

package lib;

public class MAIRAverageQue {

	private double[] que;
	private int size=0;
	private int putPosition=0;
	
	public MAIRAverageQue(int size) {
		que=new double[size];
	}
	
	public void put(double value){
		if (size < que.length){
			size++;
		} else if (putPosition >= que.length){
			putPosition=0;
		}
		que[putPosition]=value;
		putPosition++;
	}
	
	public double getAverage(){
		double sum=0.0;
		for(int i=0;i<size;i++){
			sum+=que[i];
		}
		double avg=sum/que.length;
		return avg;
	}
}
