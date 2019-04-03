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
package uk.ac.leeds.ccg.andyt.generic.data.elsa.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.data.interval.Data_IntervalLong1;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Environment;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Object;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Strings;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.io.ELSA_Files;

/**
 *
 * @author geoagdt
 */
public class ELSA_Handler extends ELSA_Object {

    public transient ELSA_Files files;
    protected transient final byte W1;
    protected transient final byte W2;
    protected transient final byte W3;
    protected transient final byte W4;
    protected transient final byte W5;
    protected transient final byte W6;
    protected transient final byte W7;
    protected transient final byte W8;

    public ELSA_Handler(ELSA_Environment e) {
        super(e);
        files = e.files;
        W1 = ELSA_Data.W1;
        W2 = ELSA_Data.W2;
        W3 = ELSA_Data.W3;
        W4 = ELSA_Data.W4;
        W5 = ELSA_Data.W5;
        W6 = ELSA_Data.W6;
        W7 = ELSA_Data.W7;
        W8 = ELSA_Data.W8;
    }

    protected Object load(byte wave, File f) {
        String m = "load " + getString0(wave, f);
        env.logStartTag(m);
        Object r = Generic_IO.readObject(f);
        env.logEndTag(m);
        return r;
    }

    protected String getString0(byte wave, File f) {
        return getString0(wave) + ELSA_Strings.symbol_space
                + ELSA_Strings.s_ELSA + ELSA_Strings.symbol_space
                + ELSA_Strings.s_file + ELSA_Strings.symbol_space + f.toString();
    }

    /**
     * A simple wrapper for
     * {@link Generic_IO#writeObject(java.lang.Object, java.io.File)}
     *
     * @param wave The wave to be cached.
     * @param f The File to cache to.
     * @param o The Object to cache.
     */
    protected void cache(byte wave, File f, Object o) {
        String m = "cache " + getString0(wave, f);
        env.logStartTag(m);
        Generic_IO.writeObject(o, f);
        env.logEndTag(m);
    }

    /**
     *
     * @param wave The wave to be cached.
     * @param type The name of the type of subset to be cached.
     * @return
     */
    public File getSubsetCacheFile(byte wave, String type) {
        return new File(files.getGeneratedELSASubsetsDir(),
                ELSA_Strings.s_W + wave + ELSA_Strings.symbol_underscore
                + type + ELSA_Files.DOT_DAT);
    }

    /**
     *
     * @param wave The wave to be cached.
     * @param type The name of the type of subset to be cached.
     * @return
     */
    public File getSubsetCacheFile2(byte wave, String type) {
        return new File(files.getGeneratedELSASubsetsDir(),
                ELSA_Strings.s_W + wave + ELSA_Strings.symbol_underscore
                + type + "2" + ELSA_Files.DOT_DAT);
    }

    /**
     *
     * @param wave The wave to be cached.
     * @param o The subset to be cached
     * @param type The name of the type of subset to be cached.
     */
    public void cacheSubset(byte wave, Object o, String type) {
        cache(wave, getSubsetCacheFile(wave, type), o);
    }

    /**
     * Writes to file the subset look ups.
     *
     * @param wave The wave of lookups from and to to be cached.
     * @param m0 The lookups from wave to (wave + 1).
     * @param m1 The lookups from (wave + 1) to wave.
     */
    public void cacheSubsetLookups(byte wave, TreeMap<Short, HashSet<Short>> m0,
            TreeMap<Short, Short> m1) {
        cache(wave, getSubsetLookupToFile(wave), m0);
        cache(wave, getSubsetLookupFromFile(wave), m1);
    }

    /**
     * @param wave
     * @return the File for a subset lookup to wave.
     */
    public File getSubsetLookupToFile(byte wave) {
        return new File(files.getGeneratedELSASubsetsDir(),
                getString0(wave) + getStringToWaveDotDat(wave + 1));
    }

    /**
     * @param wave
     * @return the File for a subset lookup from wave.
     */
    public File getSubsetLookupFromFile(byte wave) {
        return new File(files.getGeneratedELSASubsetsDir(),
                getString0(wave + 1) + getStringToWaveDotDat(wave));
    }

    public TreeMap<Short, HashSet<Short>> loadSubsetLookupTo(byte wave) {
        return (TreeMap<Short, HashSet<Short>>) Generic_IO.readObject(
                getSubsetLookupToFile(wave));
    }

    public TreeMap<Short, Short> loadSubsetLookupFrom(byte wave) {
        return (TreeMap<Short, Short>) Generic_IO.readObject(
                getSubsetLookupFromFile(wave));
    }

    protected String getString0(int wave) {
        return ELSA_Strings.s_W + wave;
    }

    protected String getString1(byte wave, short cID) {
        return getString0(wave) + ELSA_Strings.symbol_underscore + cID;
    }

    protected String getStringToWaveDotDat(int wave) {
        return ELSA_Strings.s_To + ELSA_Strings.s_W + wave + ELSA_Files.DOT_DAT;
    }

    public void cacheSubsetCollection(short cID, byte wave, Object o) {
        ELSA_Handler.this.cache(wave, getSubsetCollectionFile(cID, wave), o);
    }

    public Object loadSubsetCollection(short cID, byte wave) {
        return load(wave, getSubsetCollectionFile(cID, wave));
    }

    public File getSubsetCollectionFile(short cID, byte wave) {
        return new File(files.getGeneratedELSASubsetsDir(),
                getString1(wave, cID) + ELSA_Files.DOT_DAT);
    }

    /**
     * 1,2,4,5,6,7,8,9,10,11,12
     *
     * @return
     */
    public static ArrayList<Byte> getGORs() {
        ArrayList<Byte> gors;
        gors = new ArrayList<>();
        for (byte gor = 1; gor < 13; gor++) {
            if (gor != 3) {
                gors.add(gor);
            }
        }
        return gors;
    }

    /**
     * Init GORSubsets and GORLookups
     *
     * @param name
     * @param data
     * @param gors
     * @param subset
     * @return
     */
    public Object[] getGORSubsetsAndLookup(String name, ELSA_Data data,
            ArrayList<Byte> gors, HashSet<Short> subset) {
        Object[] r;
        r = null;
//        File f = new File(files.getOutputDataDir(), name + "GORSubsetsAndLookups.dat");
//        if (f.exists()) {
//            r = (Object[]) Generic_IO.readObject(f);
//        } else {
//            r = new Object[2];
//            HashMap<Byte, HashSet<Short>>[] r0 = new HashMap[ELSA_Data.NWAVES];
//            r[0] = r0;
//            HashMap<Short, Byte>[] r1 = new HashMap[ELSA_Data.NWAVES];
//            r[1] = r1;
//            for (byte w = 0; w < ELSA_Data.NWAVES; w++) {
//                r0[w] = new HashMap<>();
//                Iterator<Byte> ite = gors.iterator();
//                while (ite.hasNext()) {
//                    byte gor = ite.next();
//                    r0[w].put(gor, new HashSet<>());
//                }
//                r1[w] = new HashMap<>();
//            }
//            // Wave 1
//            data.data.keySet().stream().forEach(cID -> {
//                ELSA_Collection c = data.getCollection(cID);
//                c.getData().keySet().stream().forEach(CASEW1 -> {
//                    if (subset.contains(CASEW1)) {
//                        ELSA_Combined_Record cr = c.getData().get(CASEW1);
//                        byte GOR = cr.w1Record.getHhold().getGOR();
//                        Generic_Collections.addToMap(r0[0], GOR, CASEW1);
//                        r1[0].put(CASEW1, GOR);
//                    }
//                });
//                data.clearCollection(cID);
//            });
//            // Wave 2
//            data.data.keySet().stream().forEach(cID -> {
//                ELSA_Collection c = data.getCollection(cID);
//                c.getData().keySet().stream().forEach(CASEW1 -> {
//                    if (subset.contains(CASEW1)) {
//                        ELSA_Combined_Record cr = c.getData().get(CASEW1);
//                        HashMap<Short, ELSA_Wave2_Record> w2Records;
//                        w2Records = cr.w2Records;
//                        Iterator<Short> ite2 = w2Records.keySet().iterator();
//                        while (ite2.hasNext()) {
//                            Short CASEW2 = ite2.next();
//                            ELSA_Wave2_Record w2 = w2Records.get(CASEW2);
//                            byte GOR = w2.getHhold().getGOR();
//                            Generic_Collections.addToMap(r0[1], GOR, CASEW2);
//                            r1[1].put(CASEW2, GOR);
//                        }
//                    }
//                });
//                data.clearCollection(cID);
//            });
//            // Wave 3
//            data.data.keySet().stream().forEach(cID -> {
//                ELSA_Collection c = data.getCollection(cID);
//                c.getData().keySet().stream().forEach(CASEW1 -> {
//                    if (subset.contains(CASEW1)) {
//                        ELSA_Combined_Record cr = c.getData().get(CASEW1);
//                        HashMap<Short, HashMap<Short, ELSA_Wave3_Record>> w3Records;
//                        w3Records = cr.w3Records;
//                        Iterator<Short> ite2 = w3Records.keySet().iterator();
//                        while (ite2.hasNext()) {
//                            Short CASEW2 = ite2.next();
//                            HashMap<Short, ELSA_Wave3_Record> w3_2;
//                            w3_2 = w3Records.get(CASEW2);
//                            Iterator<Short> ite3 = w3_2.keySet().iterator();
//                            while (ite3.hasNext()) {
//                                Short CASEW3 = ite3.next();
//                                ELSA_Wave3_Record w3 = w3_2.get(CASEW3);
//                                byte GOR = w3.getHhold().getGOR();
//                                Generic_Collections.addToMap(r0[2], GOR, CASEW3);
//                                r1[2].put(CASEW3, GOR);
//                            }
//                        }
//                    }
//                });
//                data.clearCollection(cID);
//            });
//            // Wave 4
//            data.data.keySet().stream().forEach(cID -> {
//                ELSA_Collection c = data.getCollection(cID);
//                c.getData().keySet().stream().forEach(CASEW1 -> {
//                    if (subset.contains(CASEW1)) {
//                        ELSA_Combined_Record cr = c.getData().get(CASEW1);
//                        HashMap<Short, HashMap<Short, HashMap<Short, ELSA_Wave4_Record>>> w4Records;
//                        w4Records = cr.w4Records;
//                        Iterator<Short> ite2 = w4Records.keySet().iterator();
//                        while (ite2.hasNext()) {
//                            Short CASEW2 = ite2.next();
//                            HashMap<Short, HashMap<Short, ELSA_Wave4_Record>> w4_2;
//                            w4_2 = w4Records.get(CASEW2);
//                            Iterator<Short> ite3 = w4_2.keySet().iterator();
//                            while (ite3.hasNext()) {
//                                Short CASEW3 = ite3.next();
//                                HashMap<Short, ELSA_Wave4_Record> w4_3;
//                                w4_3 = w4_2.get(CASEW3);
//                                Iterator<Short> ite4 = w4_3.keySet().iterator();
//                                while (ite4.hasNext()) {
//                                    Short CASEW4 = ite4.next();
//                                    ELSA_Wave4_Record w4 = w4_3.get(CASEW4);
//                                    byte GOR = w4.getHhold().getGOR();
//                                    Generic_Collections.addToMap(r0[3], GOR, CASEW4);
//                                    r1[3].put(CASEW4, GOR);
//                                }
//                            }
//                        }
//                    }
//                });
//                data.clearCollection(cID);
//            });
//            // Wave 5
//            data.data.keySet().stream().forEach(cID -> {
//                ELSA_Collection c;
//                c = data.getCollection(cID);
//                c.getData().keySet().stream().forEach(CASEW1 -> {
//                    if (subset.contains(CASEW1)) {
//                        ELSA_Combined_Record cr = c.getData().get(CASEW1);
//                        HashMap<Short, HashMap<Short, HashMap<Short, HashMap<Short, ELSA_Wave5_Record>>>> w5Records;
//                        w5Records = cr.w5Records;
//                        Iterator<Short> ite2 = w5Records.keySet().iterator();
//                        while (ite2.hasNext()) {
//                            Short CASEW2 = ite2.next();
//                            HashMap<Short, HashMap<Short, HashMap<Short, ELSA_Wave5_Record>>> w5_2;
//                            w5_2 = w5Records.get(CASEW2);
//                            Iterator<Short> ite3 = w5_2.keySet().iterator();
//                            while (ite3.hasNext()) {
//                                Short CASEW3 = ite3.next();
//                                HashMap<Short, HashMap<Short, ELSA_Wave5_Record>> w5_3;
//                                w5_3 = w5_2.get(CASEW3);
//                                Iterator<Short> ite4 = w5_3.keySet().iterator();
//                                while (ite4.hasNext()) {
//                                    Short CASEW4 = ite4.next();
//                                    HashMap<Short, ELSA_Wave5_Record> w5_4;
//                                    w5_4 = w5_3.get(CASEW4);
//                                    Iterator<Short> ite5 = w5_4.keySet().iterator();
//                                    while (ite5.hasNext()) {
//                                        Short CASEW5 = ite5.next();
//                                        ELSA_Wave5_Record w5 = w5_4.get(CASEW5);
//                                        byte GOR = w5.getHhold().getGOR();
//                                        Generic_Collections.addToMap(r0[4], GOR, CASEW5);
//                                        r1[4].put(CASEW5, GOR);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                });
//                data.clearCollection(cID);
//            });
//            Generic_IO.writeObject(r, f);
//        }
        return r;
    }

    /**
     * Value label information for Government Office Regions
     *
     * @return
     */
    public static TreeMap<Byte, String> getGORNameLookup() {
        TreeMap<Byte, String> r;
        r = new TreeMap<>();
        r.put((byte) 1, "North East");
        r.put((byte) 2, "North West");
        r.put((byte) 3, "The Wirral");
        r.put((byte) 4, "Yorkshire & Humber");
        r.put((byte) 5, "East Midlands");
        r.put((byte) 6, "West Midlands");
        r.put((byte) 7, "East of England");
        r.put((byte) 8, "London");
        r.put((byte) 9, "South East");
        r.put((byte) 10, "South West");
        r.put((byte) 11, "Wales");
        r.put((byte) 12, "Scotland");
        return r;
    }

    /**
     * The lookup is the same for the first 5 waves of ELSA. From the
     * documentation we have:
     * <ul>
     * <li>Value = 1.0	Label = Less than £250</li>
     * <li>Value = 2.0	Label = £250 to £499</li>
     * <li>Value = 3.0	Label = £400[sic] to £749</li>
     * <li>Value = 4.0	Label = £750 to £999</li>
     * <li>Value = 5.0	Label = £1,000 to £1,249</li>
     * <li>Value = 6.0	Label = £1,250 to £1,499</li>
     * <li>Value = 7.0	Label = £1,500 to £1,749</li>
     * <li>Value = 8.0	Label = £1,750 to £1,999</li>
     * <li>Value = 9.0	Label = £2,000 to £2,499</li>
     * <li>Value = 10.0	Label = £2,500 to £2,999</li>
     * <li>Value = 11.0	Label = £3,000 to £3,999</li>
     * <li>Value = 12.0	Label = £4,000 to £4,999</li>
     * <li>Value = 13.0	Label = £5,000 to £7,499</li>
     * <li>Value = 14.0	Label = £7,500 to £9,999</li>
     * <li>Value = 15.0	Label = £10,000 or more</li>
     * <li>Value = -9.0	Label = Do not know</li>
     * <li>Value = -8.0	Label = Refusal</li>
     * <li>Value = -7.0	Label = Not applicable</li>
     * <li>Value = -6.0	Label = Error partial</li>
     * </ul>
     *
     * @return
     */
    public TreeMap<Byte, Data_IntervalLong1> getSEESMLookup() {
        String m = "getSEESMLookup";
        env.logStartTag(m);
        TreeMap<Byte, Data_IntervalLong1> r = new TreeMap<>();
        long d = 250L;
        long l = 0L;
        long u = d;
        for (long x = 1; x <= 9; x++) {
            r.put((byte) x, new Data_IntervalLong1(l, u));
            l = u;
            u += d;
        }
        l = 2500L;
        u = 3000L;
        r.put((byte) 10, new Data_IntervalLong1(l, u));
        d = 1000L;
        l = u;
        u += d;
        for (long x = 11; x <= 13; x++) {
            r.put((byte) x, new Data_IntervalLong1(l, u));
            l = u;
            u += d;
        }
        l = 7500L;
        u = 10000L;
        r.put((byte) 14, new Data_IntervalLong1(l, u));
        l = u;
        u = Long.MAX_VALUE;
        r.put((byte) 15, new Data_IntervalLong1(l, u));
        env.logEndTag(m);
        return r;
    }

    /**
     * For Waves 1 and 2 there is the following:
     *
     * <ul>
     * <li>Value = 1.0	Label = Less than £20,000</li>
     * <li>Value = 2.0	Label = £20,000 to £39,999</li>
     * <li>Value = 3.0	Label = £40,000 to £59,999</li>
     * <li>Value = 4.0	Label = £60,000 to £99,999</li>
     * <li>Value = 5.0	Label = £100,000 to £149,999</li>
     * <li>Value = 6.0	Label = £150,000 to £199,999</li>
     * <li>Value = 7.0	Label = £200,000 to £249,999</li>
     * <li>Value = 8.0	Label = £250,000 to £299,999</li>
     * <li>Value = 9.0	Label = £300,000 to £499,999</li>
     * <li>Value = 10.0	Label = £500,000 or more</li>
     * </ul>
     *
     * For Waves 3, 4 and 5 there is the following:
     * <ul>
     * <li>Value = 1.0	Label = Less than £60,000</li>
     * <li>Value = 2.0	Label = £60,000 to £99,999</li>
     * <li>Value = 3.0	Label = £100,000 to £149,999</li>
     * <li>Value = 4.0	Label = £150,000 to £199,999</li>
     * <li>Value = 5.0	Label = £200,000 to £249,999</li>
     * <li>Value = 6.0	Label = £250,000 to £299,999</li>
     * <li>Value = 7.0	Label = £300,000 to £349,999</li>
     * <li>Value = 8.0	Label = £350,000 to £399,999</li>
     * <li>Value = 9.0	Label = £400,000 to £499,999</li>
     * <li>Value = 10.0	Label = £500,000 to £749,999</li>
     * <li>Value = 11.0	Label = £750,000 to £999,999</li>
     * <li>Value = 12.0	Label = £1 million or more</li>
     * </ul>
     *
     * @param w
     * @return
     */
    public TreeMap<Byte, Data_IntervalLong1> getHPRICEBLookup(byte w) {
        String m = "getHPRICEBLookup";
        env.logStartTag(m);
        TreeMap<Byte, Data_IntervalLong1> r = new TreeMap<>();
        if (w == ELSA_Data.W1 || w == ELSA_Data.W2) {
            r.put((byte) 1, new Data_IntervalLong1(0L, 20000L));
            r.put((byte) 2, new Data_IntervalLong1(20000L, 40000L));
            r.put((byte) 3, new Data_IntervalLong1(40000L, 60000L));
            r.put((byte) 4, new Data_IntervalLong1(60000L, 100000L));
            r.put((byte) 5, new Data_IntervalLong1(100000L, 150000L));
            r.put((byte) 6, new Data_IntervalLong1(150000L, 200000L));
            r.put((byte) 7, new Data_IntervalLong1(200000L, 250000L));
            r.put((byte) 8, new Data_IntervalLong1(250000L, 300000L));
            r.put((byte) 9, new Data_IntervalLong1(300000L, 500000L));
            r.put((byte) 9, new Data_IntervalLong1(500000L, Long.MAX_VALUE));
        } else if (w == ELSA_Data.W3 || w == ELSA_Data.W4 || w == ELSA_Data.W5) {
            long l = 0L;
            long u = 60000L;
            r.put((byte) 1, new Data_IntervalLong1(l, u));
            l = u;
            u = 100000L;
            r.put((byte) 2, new Data_IntervalLong1(l, u));
            long d = 100000L;
            for (long x = 3; x <= 9; x++) {
                l = u;
                u += d;
                r.put((byte) x, new Data_IntervalLong1(l, u));
            }
            l = u;
            r.put((byte) 10, new Data_IntervalLong1(l, 750000L));
            l = u;
            r.put((byte) 11, new Data_IntervalLong1(l, 1000000L));
            l = u;
            r.put((byte) 12, new Data_IntervalLong1(l, Long.MAX_VALUE));
        } else {
            env.log("Exception: Unrecognised wave " + w);
        }
        env.logEndTag(m);
        return r;
    }
}
