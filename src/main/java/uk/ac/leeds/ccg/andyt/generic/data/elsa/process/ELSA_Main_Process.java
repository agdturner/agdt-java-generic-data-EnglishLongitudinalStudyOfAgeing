/*
 * Copyright 2018 geoagdt.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.andyt.generic.data.elsa.process;

import uk.ac.leeds.ccg.andyt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Environment;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.io.ELSA_Files;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Object;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.data.ELSA_Data;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.data.ELSA_Handler;

/**
 *
 * @author geoagdt
 */
public class ELSA_Main_Process extends ELSA_Object {

    // For convenience
    protected final ELSA_Data data;
    protected transient final byte NWAVES;
    protected transient final byte W1;
    protected transient final byte W2;
    protected transient final byte W3;
    protected transient final byte W4;
    protected transient final byte W5;
    protected transient final byte W6;
    protected transient final byte W7;
    protected transient final byte W8;
    protected final ELSA_Files files;

    public ELSA_Main_Process(ELSA_Environment env) {
        super(env);
        data = env.data;
        NWAVES = ELSA_Data.NWAVES;
        W1 = ELSA_Data.W1;
        W2 = ELSA_Data.W2;
        W3 = ELSA_Data.W3;
        W4 = ELSA_Data.W4;
        W5 = ELSA_Data.W5;
        W6 = ELSA_Data.W6;
        W7 = ELSA_Data.W7;
        W8 = ELSA_Data.W8;
        files = env.files;
    }

    public static void main(String[] args) {
        ELSA_Environment env = new ELSA_Environment(new Generic_Environment());
        ELSA_Main_Process p = new ELSA_Main_Process(env);
        // Main switches
        //p.doJavaCodeGeneration = true;
        
        p.run();
    }

    public void run() {
        String m = "run";
        env.logStartTag(m);
        if (doJavaCodeGeneration) {
            runJavaCodeGeneration();
        }
//        ELSA_HHOLD_Handler hH = new ELSA_HHOLD_Handler(env);
//        int chunkSize = 256; //1024; 512; 256;
//        if (doLoadAllHouseholdsRecords) {
//            loadAllHouseholdRecords(hH);
//        }
//        if (doLoadHouseholdsAndIndividualsInAllWaves) {
//            loadHouseholdsInAllWaves(hH);
//            mergePersonAndHouseholdDataIntoCollections(ELSA_Strings.s_InW1W2W3W4W5, hH, chunkSize);
//        }
//        if (doLoadHouseholdsInPairedWaves) {
//            if (true) {
//                Object[] W2InW1 = hH.loadHouseholdsInPreviousWave(W2);
//                TreeMap<Short, WaAS_Wave2_HHOLD_Record> W2InW1Recs;
//                W2InW1Recs = (TreeMap<Short, WaAS_Wave2_HHOLD_Record>) W2InW1[0];
//                TreeSet<Short>[] W2InW1Sets = (TreeSet<Short>[]) W2InW1[1];
//                TreeSet<Short> W2InW1CASEW1 = W2InW1Sets[0];
//                Object[] W1InW2 = hH.loadW1(W2InW1CASEW1, ELSA_Strings.s_InW2);
//                TreeMap<Short, WaAS_Wave1_HHOLD_Record> W1InW2Recs;
//                W1InW2Recs = (TreeMap<Short, WaAS_Wave1_HHOLD_Record>) W1InW2[0];
//                env.log(W2InW1Recs.size() + "\t W2InW1Recs.size()");
//                env.log(W1InW2Recs.size() + "\t W1InW2Recs.size()");
//
//            }
//            if (true) {
//                Object[] W3InW2 = hH.loadHouseholdsInPreviousWave(W3);
//                TreeMap<Short, WaAS_Wave3_HHOLD_Record> W3InW2Recs;
//                W3InW2Recs = (TreeMap<Short, WaAS_Wave3_HHOLD_Record>) W3InW2[0];
//                TreeSet<Short>[] W3InW2Sets = (TreeSet<Short>[]) W3InW2[1];
//                TreeSet<Short> W3InW2CASEW2 = W3InW2Sets[1];
//                Object[] W2InW3 = hH.loadW2(W3InW2CASEW2, ELSA_Strings.s_InW3);
//                TreeMap<Short, WaAS_Wave1_HHOLD_Record> W2InW3Recs;
//                W2InW3Recs = (TreeMap<Short, WaAS_Wave1_HHOLD_Record>) W2InW3[0];
//                env.log(W3InW2Recs.size() + "\t W3InW2Recs.size()");
//                env.log(W2InW3Recs.size() + "\t W2InW3Recs.size()");
//            }
//            if (true) {
//                Object[] W4InW3 = hH.loadHouseholdsInPreviousWave(W4);
//                TreeMap<Short, WaAS_Wave4_HHOLD_Record> W4InW3Recs;
//                W4InW3Recs = (TreeMap<Short, WaAS_Wave4_HHOLD_Record>) W4InW3[0];
//                TreeSet<Short>[] W4InW3Sets = (TreeSet<Short>[]) W4InW3[1];
//                TreeSet<Short> W4InW3CASEW3 = W4InW3Sets[2];
//                Object[] W3InW4 = hH.loadW3(W4InW3CASEW3, ELSA_Strings.s_InW4);
//                TreeMap<Short, WaAS_Wave1_HHOLD_Record> W3InW4Recs;
//                W3InW4Recs = (TreeMap<Short, WaAS_Wave1_HHOLD_Record>) W3InW4[0];
//                env.log(W4InW3Recs.size() + "\t W4InW3Recs.size()");
//                env.log(W3InW4Recs.size() + "\t W3InW4Recs.size()");
//            }
//            if (true) {
//                Object[] W5InW4 = hH.loadHouseholdsInPreviousWave(W5);
//                TreeMap<Short, WaAS_Wave5_HHOLD_Record> W5InW4Recs;
//                W5InW4Recs = (TreeMap<Short, WaAS_Wave5_HHOLD_Record>) W5InW4[0];
//                TreeSet<Short>[] W5InW4Sets = (TreeSet<Short>[]) W5InW4[1];
//                TreeSet<Short> W5InW4CASEW4 = W5InW4Sets[3];
//                Object[] W4InW5 = hH.loadW4(W5InW4CASEW4, ELSA_Strings.s_InW5);
//                TreeMap<Short, WaAS_Wave1_HHOLD_Record> W4InW5Recs;
//                W4InW5Recs = (TreeMap<Short, WaAS_Wave1_HHOLD_Record>) W4InW5[0];
//                env.log(W5InW4Recs.size() + "\t W5InW4Recs.size()");
//                env.log(W4InW5Recs.size() + "\t W4InW5Recs.size()");
//            }
//            mergePersonAndHouseholdDataIntoCollections(//indir, outdir,
//                    ELSA_Strings.s_Paired, hH, chunkSize);
//        }
//        HashSet<Short> subset = hH.getStableHouseholdCompositionSubset(data);
//        initDataSimple(subset);
        env.cacheData();
        env.logEndTag(m);
        env.ge.closeLog(env.logID);
    }
    
    /**
     * Method for running JavaCodeGeneration
     */
    public void runJavaCodeGeneration() {
        ELSA_JavaCodeGenerator.main(null);
    }

    /**
     * Read input data and create subsets. The subsets are for records in all
     * waves. Organise for person records that each subset is split into
     * separate files such that these files neatly aggregate into those
     * corresponding to household collections. The collections will be merged
     * one by one in doDataProcessingStep2.
     *
     * @param h hholdHandler
     */
    public void loadHouseholdsInAllWaves(ELSA_Handler h) {
        String m0 = "loadHouseholdsInAllWaves";
        env.logStartTag(m0);
        env.logEndTag(m0);
    }
    
    boolean doJavaCodeGeneration = false;

}
