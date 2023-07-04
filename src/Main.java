/*
*
*
* Mohammad Parsa Etemadheravi - 9812762441
*
*
* */

import SJF.SimpleExampleSJF;
import LJF.SimpleExampleLJF;
import SSTF.SimpleExampleSSTF;
import utils.Calculator;

public class Main {
    public static void main(String[] args) throws Exception {
        String CyberShake="data/workflows/CyberShake_500_1.xml";
        String LIGO="data/workflows/LIGO_500_1.xml";
        String Montage="data/workflows/Montage_500_1.xml";
        String SIPHT="data/workflows/SIPHT_500_1.xml";

        // Initial depth and critical path for all workflowss
        Calculator LIGOCalculator=new Calculator("ligo");
        Calculator cyberShakeCalculator=new Calculator("cybershake");
        Calculator montageCalculator=new Calculator("montage");
        Calculator siphtCalculator=new Calculator("sipht");



//     SimpleExampleSJF.run(CyberShake,cyberShakeCalculator);//
//         SimpleExampleLJF.run(CyberShake,cyberShakeCalculator);//
//         SimpleExampleSSTF.run(CyberShake, cyberShakeCalculator);//
//         SimpleExampleSJF.run(LIGO,LIGOCalculator);//
         SimpleExampleLJF.run(LIGO,LIGOCalculator);//

//         SimpleExampleSSTF.run(LIGO, LIGOCalculator);//

//         SimpleExampleSJF.run(Montage,montageCalculator);//
//         SimpleExampleLJF.run(Montage,montageCalculator);//
//        SimpleExampleSSTF.run(Montage, montageCalculator);//
//         SimpleExampleSJF.run(SIPHT,siphtCalculator);//
//         SimpleExampleLJF.run(SIPHT,siphtCalculator);//
//         SimpleExampleSSTF.run(SIPHT, siphtCalculator);//
    }
}
  