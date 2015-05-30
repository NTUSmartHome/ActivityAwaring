/* 
 * Copyright (C) 2014 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.datumbox;

import java.awt.List;
import java.util.ArrayList;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.datumbox.framework.machinelearning.classification.SupportVectorMachine;
import com.datumbox.framework.machinelearning.clustering.GaussianDPMM;

/**
 *
 * @author bbriniotis
 */
public class Datumbox {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Datumbox Framework Main");
        
        //SupportVectorMachine svm1 = new SupportVectorMachine("1");
        GaussianDPMM dpmm1 = new GaussianDPMM("0");
        
    }
    
}