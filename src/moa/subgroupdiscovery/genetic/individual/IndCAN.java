/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.individual;

import moa.subgroupdiscovery.genetic.Individual;
import com.yahoo.labs.samoa.instances.Instance;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import moa.subgroupdiscovery.CromCAN;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.qualitymeasures.Confidence;
import moa.subgroupdiscovery.qualitymeasures.NULL;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 *
 * @author agvico
 */
public class IndCAN extends Individual<Integer> {

    public IndCAN(int length, int neje, int clas) {
        ArrayList<QualityMeasure> objectivesArray = StreamMOEAEFEP.getObjectivesArray();

        try {
            tamano = length;
            chromosome = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                chromosome.add(0);
            }
            medidas = new ArrayList<>();
            objs = new ArrayList<>();
            for (int i = 0; i < objectivesArray.size(); i++) {
                if (!(objectivesArray.get(i) instanceof NULL)) {
                    objs.add(objectivesArray.get(i).clone());
                }
            }

            conf = new Confidence();
            diversityMeasure = (QualityMeasure) StreamMOEAEFEP.getDiversityMeasure().getClass().newInstance();
            evaluado = false;
            cubre = new BitSet(neje);

            crowdingDistance = 0.0;
            n_eval = 0;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(moa.subgroupdiscovery.IndCAN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Integer getCromElem(int pos) {
        return chromosome.get(pos);
    }

    @Override
    public void setCromElem(int pos, Integer val) {
        chromosome.set(pos, val);
    }

    @Override
    public void copyIndiv(Individual indi, int nobj, int neje) {
        if (!(indi instanceof IndCAN)) {
            try {
                throw new InvalidClassException("copyIndiv in IndCAN: \"indi\" is not of class IndCAN");
            } catch (InvalidClassException ex) {
                Logger.getLogger(IndDNF.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(2);
            }
        }

        IndCAN a = (IndCAN) indi;
        for (int i = 0; i < this.getTamano(); i++) {
            this.setCromElem(i, a.getCromElem(i));
        }

        this.setIndivEvaluated(a.getIndivEvaluated());
        this.getCubre().clear(0, neje);
        this.getCubre().or(a.getCubre());

        this.setIndivEvaluated(a.getIndivEvaluated());
        this.setCubre((BitSet) a.getCubre().clone());
        this.setCrowdingDistance(a.getCrowdingDistance());
        this.setRank(a.getRank());

        this.setObjs(new ArrayList<>());
        for (QualityMeasure q : a.getObjs()) {
            QualityMeasure aux = q.clone();

            this.getObjs().add(aux);
        }

        this.setMedidas(new ArrayList<>());
        for (QualityMeasure q : a.getMedidas()) {
            QualityMeasure aux = q.clone();

            this.getMedidas().add(aux);
        }

        this.setConf((Confidence) a.getConf().clone());

        this.setClas(a.getClas());

        this.setNEval(a.getNEval());
    }

    @Override
    public int NumInterv(double valor, int num_var, Instance inst) {
        float pertenencia = 0, new_pert = 0;
        int interv = -1;

        for (int i = 0; i < StreamMOEAEFEP.nLabel; i++) {
            new_pert = StreamMOEAEFEP.Fuzzy(num_var, i, valor);
            if (new_pert > pertenencia) {
                interv = i;
                pertenencia = new_pert;
            }
        }
        return interv;
    }

    @Override
    public String toString(Instance inst) {
        String content = "";
        for (int i = 0; i < inst.numInputAttributes(); i++) {
            if (inst.attribute(i).isNominal()) {
                // Discrete variable
                if (this.getCromElem(i) < inst.attribute(i).numValues()) {
                    content += "\tVariable " + inst.attribute(i).name() + " = ";
                    content += inst.attribute(i).value(this.getCromElem(i)) + "\n";
                }
            } else {
                // Continuous variable
                if (this.getCromElem(i) < StreamMOEAEFEP.nLabel) {
                    content += "Label " + this.getCromElem(i);
                    content += " (" + StreamMOEAEFEP.baseDatos[i][this.getCromElem(i)].getX0();
                    content += " " + StreamMOEAEFEP.baseDatos[i][this.getCromElem(i)].getX1();
                    content += " " + StreamMOEAEFEP.baseDatos[i][this.getCromElem(i)].getX3() + ")\n";
                }
            }
        }
        content += "\tConsecuent: " + inst.outputAttribute(0).value(getClas());
        return content;
    }

    @Override
    public int getNumVars() {
        int nVars = 0;
        for (int i = 0; i < chromosome.size(); i++) {
            if (StreamMOEAEFEP.instancia.attribute(i).isNominal()) {
                if (this.getCromElem(i) < StreamMOEAEFEP.instancia.attribute(i).numValues()) {
                    nVars++;
                }
            } else {
                if (this.getCromElem(i) < StreamMOEAEFEP.nLabel) {
                    nVars++;
                }
            }
        }

        return nVars;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < chromosome.size(); i++) {
            if (StreamMOEAEFEP.instancia.attribute(i).isNominal()) {
                if (this.getCromElem(i) < StreamMOEAEFEP.instancia.attribute(i).numValues()) {
                    return false;
                }
            } else {
                if (this.getCromElem(i) < StreamMOEAEFEP.nLabel) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean getCromGeneElem(int pos, int elem) {
        throw new UnsupportedOperationException("IndCAN does not contains additional elements on its genes."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCromGeneElem(int pos, int elem, boolean val) {
        throw new UnsupportedOperationException("IndCAN does not contains additional elements on its genes."); //To change body of generated methods, choose Tools | Templates.
    }

}
