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

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Environment;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.io.ELSA_Files;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Object;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Strings;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.data.ELSA_Data;
import uk.ac.leeds.ccg.andyt.math.Math_Byte;
import uk.ac.leeds.ccg.andyt.math.Math_Double;
import uk.ac.leeds.ccg.andyt.math.Math_Integer;
import uk.ac.leeds.ccg.andyt.math.Math_Short;

/**
 * This class produces source code for loading survey data. Each respondent will
 * have a unique value for IDAUNIQ, which will remain constant.
 *
 *
 * Source code classes written in order to load the Wealth and Assets Survey
 * (ELSA) household data is written to
 * uk.ac.leeds.ccg.andyt.generic.data.elsa.data.hhold. Source code classes
 * written in order to load the ELSA person data is written to
 * uk.ac.leeds.ccg.andyt.generic.data.elsa.data.person.
 *
 * As these survey data contained many variables, it was thought best to write
 * some code that wrote some code to load these data and provide access to the
 * variables. Most variables are loaded as Double types. Some such as dates have
 * been loaded as String types. There are documents:
 * data\input\ELSA\UKDA-7215-tab\mrdoc\pdf\7215_was_questionnaire_wave_1.pdf
 * data\input\ELSA\UKDA-7215-tab\mrdoc\pdf\7215_was_questionnaire_wave_2.pdf
 * data\input\ELSA\UKDA-7215-tab\mrdoc\pdf\7215_was_questionnaire_wave_3.pdf
 * data\input\ELSA\UKDA-7215-tab\mrdoc\pdf\7215_was_questionnaire_wave_4.pdf
 * data\input\ELSA\UKDA-7215-tab\mrdoc\pdf\7215_was_questionnaire_wave_5.pdf
 * that detail what values are expected from what variables. Another way to
 * create the data loading classes would be to parse this document. A thorough
 * job of exploring these data would check the data values to make sure that
 * they conformed to these schemas. This would also allow the variables to be
 * stored in the most appropriate way (e.g. as an integer, double, String, date
 * etc.).
 *
 * @author geoagdt
 */
public class ELSA_JavaCodeGenerator extends ELSA_Object {

    // For convenience
    public ELSA_Files files;

    protected ELSA_JavaCodeGenerator() {
        super();
        files = env.files;
    }

    public ELSA_JavaCodeGenerator(ELSA_Environment env) {
        super(env);
        files = env.files;
    }

    public static void main(String[] args) {
        ELSA_JavaCodeGenerator p = new ELSA_JavaCodeGenerator(
                new ELSA_Environment(new Generic_Environment()));
        int nwaves = ELSA_Data.NWAVES;
        Object[] fieldTypes = p.getFieldTypes(nwaves);
        p.run(fieldTypes, nwaves);
    }

    /**
     * Pass through the data and works out what numeric type is best to store
     * each field in the data.
     *
     * @param nwaves
     * @return keys are standardised field names, value is: 0 if field is to be
     * represented by a String; 1 if field is to be represented by a double; 2
     * if field is to be represented by a int; 3 if field is to be represented
     * by a short; 4 if field is to be represented by a byte; 5 if field is to
     * be represented by a boolean.
     */
    protected Object[] getFieldTypes(int nwaves) {
        Object[] r = new Object[4];
        File indir = files.getInputELSADir();
        File generateddir = files.getGeneratedELSADir();
        File outdir = new File(generateddir, ELSA_Strings.s_Subsets);
        outdir.mkdirs();
        HashMap<String, Integer>[] allFieldTypes = new HashMap[nwaves];
        String[][] headers = new String[nwaves][];
        HashMap<String, Byte>[] v0ms = new HashMap[nwaves];
        HashMap<String, Byte>[] v1ms = new HashMap[nwaves];
        for (int w = 0; w < nwaves; w++) {
            Object[] t = loadTest(w + 1, indir);
            HashMap<String, Integer> fieldTypes = new HashMap<>();
            allFieldTypes[w] = fieldTypes;

            ArrayList<String> fields = (ArrayList<String>) t[0];
            headers[w] = fields.toArray(new String[fields.size()]);
            ArrayList<Boolean> strings = (ArrayList<Boolean>) t[1];
            ArrayList<Boolean> doubles = (ArrayList<Boolean>) t[2];
            ArrayList<Boolean> ints = (ArrayList<Boolean>) t[3];
            ArrayList<Boolean> shorts = (ArrayList<Boolean>) t[4];
            ArrayList<Boolean> bytes = (ArrayList<Boolean>) t[5];
            ArrayList<Boolean> booleans = (ArrayList<Boolean>) t[6];

            HashMap<String, Byte> v0m = (HashMap<String, Byte>) t[7];
            HashMap<String, Byte> v1m = (HashMap<String, Byte>) t[8];
            v0ms[w] = v0m;
            v1ms[w] = v1m;

            for (int i = 0; i < headers[w].length; i++) {
                String field = fields.get(i);
                String m = field + " " + i + " is a ";
                if (strings.get(i)) {
                    m += "String";
                    fieldTypes.put(field, 0);
                } else {
                    if (doubles.get(i)) {
                        m += "double";
                        fieldTypes.put(field, 1);
                    } else {
                        if (ints.get(i)) {
                            m += "int";
                            fieldTypes.put(field, 2);
                        } else {
                            if (shorts.get(i)) {
                                m += "short";
                                fieldTypes.put(field, 3);
                            } else {
                                if (bytes.get(i)) {
                                    m += "byte";
                                    fieldTypes.put(field, 4);
                                } else {
                                    if (booleans.get(i)) {
                                        m += "boolean";
                                        fieldTypes.put(field, 5);
                                    } else {
                                        try {
                                            throw new Exception("unrecognised type");
                                        } catch (Exception ex) {
                                            ex.printStackTrace(System.err);
                                            Logger.getLogger(ELSA_JavaCodeGenerator.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                env.log(m);
            }
        }
        HashMap<String, Integer> consolidatedFieldTypes = new HashMap<>();
        consolidatedFieldTypes.putAll(allFieldTypes[0]);
        for (int w = 1; w < nwaves; w++) {
            HashMap<String, Integer> fieldTypes = allFieldTypes[w];
            Iterator<String> ite = fieldTypes.keySet().iterator();
            while (ite.hasNext()) {
                String field = ite.next();
                int fieldType = fieldTypes.get(field);
                if (consolidatedFieldTypes.containsKey(field)) {
                    int consolidatedFieldType = consolidatedFieldTypes.get(field);
                    if (fieldType != consolidatedFieldType) {
                        consolidatedFieldTypes.put(field,
                                Math.min(fieldType, consolidatedFieldType));
                    }
                } else {
                    consolidatedFieldTypes.put(field, fieldType);
                }
            }
        }
        r[0] = consolidatedFieldTypes;
        r[1] = headers;
        r[2] = v0ms;
        r[3] = v1ms;
        return r;
    }

    /**
     *
     * @param wave
     * @param indir
     * @return
     */
    public Object[] loadTest(int wave, File indir) {
        String m = "loadTest(wave " + wave + ", indir "
                + indir.toString() + ")";
        env.logStartTag(m);
        Object[] r = new Object[10];
        HashMap<String, Byte> v0m = new HashMap<>();
        HashMap<String, Byte> v1m = new HashMap<>();
        ArrayList<File> fs = getInputFiles(wave, indir);
        ArrayList<String> fieldsAll = new ArrayList<>();
        ArrayList<Boolean> stringsAll = new ArrayList<>();
        ArrayList<Boolean> doublesAll = new ArrayList<>();
        ArrayList<Boolean> intsAll = new ArrayList<>();
        ArrayList<Boolean> shortsAll = new ArrayList<>();
        ArrayList<Boolean> bytesAll = new ArrayList<>();
        ArrayList<Boolean> booleansAll = new ArrayList<>();
        HashMap<String, Byte> v0mAll = new HashMap<>();
        HashMap<String, Byte> v1mAll = new HashMap<>();
        Iterator<File> itef = fs.iterator();
        while (itef.hasNext()) {
            File f = itef.next();
            BufferedReader br = env.ge.io.getBufferedReader(f);
            String line = br.lines().findFirst().get();
            String[] fields = parseHeader(line, wave);
            int n = fields.length;
            boolean[] strings = new boolean[n];
            boolean[] doubles = new boolean[n];
            boolean[] ints = new boolean[n];
            boolean[] shorts = new boolean[n];
            boolean[] bytes = new boolean[n];
            boolean[] booleans = new boolean[n];
            byte[] v0 = new byte[n];
            byte[] v1 = new byte[n];
            for (int i = 0; i < n; i++) {
                strings[i] = false;
                doubles[i] = false;
                ints[i] = false;
                shorts[i] = false;
                //bytes[i] = true;
                bytes[i] = false;
                booleans[i] = true;
                v0[i] = Byte.MIN_VALUE;
                v1[i] = Byte.MIN_VALUE;
            }
            br.lines().skip(1).forEach(l -> {
                String[] split = l.split("\t");
                for (int i = 0; i < n; i++) {
                    parse(split[i], fields[i], i, strings, doubles, ints, shorts,
                            bytes, booleans, v0, v1, v0m, v1m);
                }
            });
            /**
             * Order v0m and v1m so that v0m always has the smaller value and
             * v1m the larger.
             */
            Iterator<String> ite = v0m.keySet().iterator();
            while (ite.hasNext()) {
                String s = ite.next();
                byte v00 = v0m.get(s);
                if (v1m.containsKey(s)) {
                    byte v11 = v1m.get(s);
                    if (v00 > v11) {
                        v0m.put(s, v11);
                        v1m.put(s, v00);
                    }
                }
            }
            for (int i = 0; i < fields.length; i++) {
                fieldsAll.add(fields[i]);
                stringsAll.add(strings[i]);
                doublesAll.add(doubles[i]);
                intsAll.add(ints[i]);
                shortsAll.add(shorts[i]);
                bytesAll.add(bytes[i]);
                booleansAll.add(booleans[i]);
                v0mAll.putAll(v0m);
                v1mAll.putAll(v1m);
            }
        }
        r[0] = fieldsAll;
        r[1] = stringsAll;
        r[2] = doublesAll;
        r[3] = intsAll;
        r[4] = shortsAll;
        r[5] = bytesAll;
        r[6] = booleansAll;
        r[7] = v0mAll;
        r[8] = v1mAll;
        env.logEndTag(m);
        return r;
    }

    /**
     * <ul>
     * <li>For wave 1 there are 5 different data files.</li>
     * <li>For wave 2 there are 8 different data files.</li>
     * <li>For wave 3 there are 7 different data files.</li>
     * <li>For wave 4 there are 6 different data files.</li>
     * <li>For wave 5 there are 5 different data files.</li>
     * <li>For wave 6 there are 5 different data files.</li>
     * <li>For wave 7 there are 4 different data files.</li>
     * <li>For wave 8 there are 5 different data files.</li>
     * </ul>
     *
     * @param wave
     * @param indir
     * @return list of input files for wave.
     */
    public ArrayList<File> getInputFiles(int wave, File indir) {
        ArrayList<File> r = new ArrayList<>();
        String filename0 = ELSA_Strings.s_wave + ELSA_Strings.symbol_underscore
                + wave + ELSA_Strings.symbol_underscore;
        String filename;
        switch (wave) {
            case 1:
                for (int type = 0; type < 5; type++) {
                    filename = filename0;
                    switch (type) {
                        case 0:
                            filename += ELSA_Strings.s_core + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_data + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_v + ELSA_Strings.symbol_3;
                            break;
                        case 1:
                            filename += "financial_derived_variables";
                            break;
                        case 2:
                            filename += "ifs_derived_variables";
                            break;
                        case 3:
                            filename += "pension_grid";
                            break;
                        case 4:
                            filename += "pension_wealth_v2";
                            break;
                    }
                    filename += ".tab";
                    r.add(new File(indir, filename));
                }
                break;
            case 2:
                for (int type = 0; type < 8; type++) {
                    filename = filename0;
                    switch (type) {
                        case 0:
                            filename += ELSA_Strings.s_core + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_data + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_v + ELSA_Strings.symbol_4;
                            break;
                        case 1:
                            filename += "financial_derived_variables";
                            break;
                        case 2:
                            filename += "ifs_derived_variables";
                            break;
                        case 3:
                            filename += "pension_grid_v3";
                            break;
                        case 4:
                            filename += "pension_wealth";
                            break;
                        case 5:
                            filename += "mortgage_data";
                            break;
                        case 6:
                            filename += "nurse_data_v2";
                            break;
                        case 7:
                            filename += "ryff_data";
                            break;
                    }
                    filename += ".tab";
                    r.add(new File(indir, filename));
                }
                break;
            case 3:
                for (int type = 0; type < 7; type++) {
                    filename = filename0;
                    switch (type) {
                        case 0:
                            filename += ELSA_Strings.s_elsa + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_data + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_v + ELSA_Strings.symbol_4;
                            break;
                        case 1:
                            filename += "financial_derived_variables";
                            break;
                        case 2:
                            filename += "ifs_derived_variables";
                            break;
                        case 3:
                            filename += "pensiongrid_v4";
                            break;
                        case 4:
                            filename += "pension_wealth";
                            break;
                        case 5:
                            filename += "life_history_data";
                            break;
                        case 6:
                            filename += "mortgage_grid";
                            break;
                    }
                    filename += ".tab";
                    r.add(new File(indir, filename));
                }
                break;
            case 4:
                for (int type = 0; type < 6; type++) {
                    filename = filename0;
                    switch (type) {
                        case 0:
                            filename += ELSA_Strings.s_elsa + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_data + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_v + ELSA_Strings.symbol_3;
                            break;
                        case 1:
                            filename += "financial_derived_variables";
                            break;
                        case 2:
                            filename += "ifs_derived_variables";
                            break;
                        case 3:
                            filename += "pension_grid_v1";
                            break;
                        case 4:
                            filename += "pension_wealth";
                            break;
                        case 5:
                            filename += "nurse_data";
                            break;
                    }
                    filename += ".tab";
                    r.add(new File(indir, filename));
                }
                break;
            case 5:
                for (int type = 0; type < 5; type++) {
                    filename = filename0;
                    switch (type) {
                        case 0:
                            filename += ELSA_Strings.s_elsa + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_data + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_v + ELSA_Strings.symbol_4;
                            break;
                        case 1:
                            filename += "financial_derived_variables";
                            break;
                        case 2:
                            filename += "ifs_derived_variables";
                            break;
                        case 3:
                            filename += "pension_grid_v3";
                            break;
                        case 4:
                            filename += "pension_wealth";
                            break;
                    }
                    filename += ".tab";
                    r.add(new File(indir, filename));
                }
                break;
            case 6:
                for (int type = 0; type < 5; type++) {
                    filename = filename0;
                    switch (type) {
                        case 0:
                            filename += ELSA_Strings.s_elsa + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_data + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_v + ELSA_Strings.symbol_2;
                            break;
                        case 1:
                            filename += "financial_derived_variables";
                            break;
                        case 2:
                            filename += "ifs_derived_variables";
                            break;
                        case 3:
                            filename += "pensiongrid_archive_v1";
                            break;
                        case 4:
                            filename += "elsa_nurse_data_v2";
                            break;
                    }
                    filename += ".tab";
                    r.add(new File(indir, filename));
                }
                break;
            case 7:
                for (int type = 0; type < 4; type++) {
                    filename = filename0;
                    switch (type) {
                        case 0:
                            filename += ELSA_Strings.s_elsa + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_data;
                            break;
                        case 1:
                            filename += "financial_derived_variables";
                            break;
                        case 2:
                            filename += "ifs_derived_variables";
                            break;
                        case 3:
                            filename += "pensiongrid_archive_v2_final";
                            break;
                    }
                    filename += ".tab";
                    r.add(new File(indir, filename));
                }
                break;
            case 8:
                for (int type = 0; type < 5; type++) {
                    filename = filename0;
                    filename += ELSA_Strings.s_elsa + ELSA_Strings.symbol_underscore;
                    switch (type) {
                        case 0:
                            filename += ELSA_Strings.s_data + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_eul + ELSA_Strings.symbol_underscore
                                    + ELSA_Strings.s_v + ELSA_Strings.symbol_2;
                            break;
                        case 1:
                            filename += "financial_dvs_eul_v1";
                            break;
                        case 2:
                            filename += "ifs_dvs_eul_v1";
                            break;
                        case 3:
                            filename += "pensiongrid_eul_v1";
                            break;
                        case 4:
                            filename += "nurse_data_eul_v1";
                            break;
                    }
                    filename += ".tab";
                    r.add(new File(indir, filename));
                }
                break;
            default:
                break;
        }
        return r;
    }

    /**
     * If s can be represented as a byte reserving Byte.Min_Value for a
     * noDataValue,
     *
     * @param s
     * @param field
     * @param index
     * @param strings
     * @param doubles
     * @param ints
     * @param shorts
     * @param bytes
     * @param booleans
     * @param v0
     * @param v1
     * @param v0m
     * @param v1m
     */
    public void parse(String s, String field, int index, boolean[] strings,
            boolean[] doubles, boolean[] ints, boolean[] shorts,
            boolean[] bytes, boolean[] booleans, byte[] v0, byte[] v1,
            HashMap<String, Byte> v0m, HashMap<String, Byte> v1m) {
        if (!s.trim().isEmpty()) {
            if (!strings[index]) {
                if (doubles[index]) {
                    doDouble(s, index, strings, doubles);
                } else {
                    if (ints[index]) {
                        doInt(s, index, strings, doubles, ints);
                    } else {
                        if (shorts[index]) {
                            doShort(s, index, strings, doubles, ints, shorts);
                        } else {
                            if (bytes[index]) {
                                doByte(s, index, strings, doubles, ints,
                                        shorts, bytes);
                            } else {
                                if (booleans[index]) {
                                    if (Math_Byte.isByte(s)) {
                                        byte b = Byte.valueOf(s);
                                        if (v0[index] > Byte.MIN_VALUE) {
                                            if (!(b == v0[index])) {
                                                if (v1[index] > Byte.MIN_VALUE) {
                                                    if (!(b == v1[index])) {
                                                        booleans[index] = false;
                                                        bytes[index] = true;
                                                    }
                                                } else {
                                                    v1[index] = b;
                                                    v1m.put(field, b);
                                                }
                                            }
                                        } else {
                                            v0[index] = b;
                                            v0m.put(field, b);
                                        }
                                    } else {
                                        booleans[index] = false;
                                        shorts[index] = true;
                                        doShort(s, index, strings, doubles, ints,
                                                shorts);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void doByte(String s, int index, boolean[] strings,
            boolean[] doubles, boolean[] ints, boolean[] shorts,
            boolean[] bytes) {
        if (!Math_Byte.isByte(s)) {
            bytes[index] = false;
            shorts[index] = true;
            doShort(s, index, strings, doubles, ints, shorts);
        }
    }

    protected void doShort(String s, int index, boolean[] strings,
            boolean[] doubles, boolean[] ints, boolean[] shorts) {
        if (!Math_Short.isShort(s)) {
            shorts[index] = false;
            ints[index] = true;
            doInt(s, index, strings, doubles, ints);
        }
    }

    protected void doInt(String s, int index, boolean[] strings,
            boolean[] doubles, boolean[] ints) {
        if (!Math_Integer.isInt(s)) {
            ints[index] = false;
            doubles[index] = true;
            doDouble(s, index, strings, doubles);
        }
    }

    protected void doDouble(String s, int index, boolean[] strings,
            boolean[] doubles) {
        if (!Math_Double.isDouble(s)) {
            doubles[index] = false;
            strings[index] = true;
        }
    }

    public void run(Object[] types, int nwaves) {
        HashMap<String, Integer> fieldTypes;
        fieldTypes = (HashMap<String, Integer>) types[0];
        String[][] headers = (String[][]) types[1];
        HashMap<String, Byte>[] v0ms  = (HashMap<String, Byte>[]) types[2];
        HashMap<String, Byte>[] v1ms  = (HashMap<String, Byte>[]) types[3];

        TreeSet<String>[] fields = getFields(headers);

        HashMap<String, Byte> v0m0;
        v0m0 = setCommonBooleanMaps(v0ms, v1ms, fields, fieldTypes);

        File outdir;
        outdir = new File(files.getDataDir(), "..");
        outdir = new File(outdir, ELSA_Strings.s_src);
        outdir = new File(outdir, ELSA_Strings.s_main);
        outdir = new File(outdir, ELSA_Strings.s_java);
        outdir = new File(outdir, ELSA_Strings.s_uk);
        outdir = new File(outdir, ELSA_Strings.s_ac);
        outdir = new File(outdir, ELSA_Strings.s_leeds);
        outdir = new File(outdir, ELSA_Strings.s_ccg);
        outdir = new File(outdir, ELSA_Strings.s_andyt);
        outdir = new File(outdir, ELSA_Strings.s_generic);
        outdir = new File(outdir, ELSA_Strings.s_data);
        outdir = new File(outdir, ELSA_Strings.s_elsa);
        outdir = new File(outdir, ELSA_Strings.s_data);
        outdir = new File(outdir, ELSA_Strings.s_generated);
        outdir.mkdirs();
        String packageName;
        packageName = "uk.ac.leeds.ccg.andyt.generic.data.elsa.data.generated";

        File fout;
        PrintWriter pw;
        int wave;
        String className;
        String extendedClassName;
        String prepend = ELSA_Strings.s_ELSA + ELSA_Strings.symbol_underscore;

        for (int w = 0; w < fields.length; w++) {
            if (w < nwaves) {
                // Non-abstract classes
                wave = w + 1;
                HashMap<String, Byte> v0m;
                v0m = v0ms[w];
                className = prepend + "Wave" + wave + "_Record";
                fout = new File(outdir, className + ".java");
                pw = env.ge.io.getPrintWriter(fout, false);
                writeHeaderPackageAndImports(pw, packageName, "");
                extendedClassName = prepend + "Waves1To8_Record";
                printClassDeclarationSerialVersionUID(pw, packageName,
                        className, "", extendedClassName);
                // Print Field Declarations Inits And Getters
                printFieldDeclarationsInitsAndGetters(pw, fields[w], fieldTypes,
                        v0m);
                // Constructor
                pw.println("public " + className + "(String line) {");
                pw.println("s = line.split(\"\\t\");");
                for (int j = 0; j < headers[w].length; j++) {
                    pw.println("init" + headers[w][j] + "(s[" + j + "]);");
                }
                pw.println("}");
                pw.println("}");
                pw.close();
            } else {
                // Abstract classes
                pw = null;
                className = prepend + "Waves1To8_Record";
                fout = new File(outdir, className + ".java");
                pw = env.ge.io.getPrintWriter(fout, false);
                writeHeaderPackageAndImports(pw, packageName,
                        "java.io.Serializable");
                printClassDeclarationSerialVersionUID(pw, packageName,
                        className, "Serializable", "");
                pw.println("protected String[] s;");
                // Print Field Declarations Inits And Getters
                printFieldDeclarationsInitsAndGetters(pw, fields[w], fieldTypes, v0m0);
                pw.println("}");
                pw.close();
            }
        }
    }

    /**
     *
     * @param pw
     * @param packageName
     * @param imports
     */
    public void writeHeaderPackageAndImports(PrintWriter pw,
            String packageName, String imports) {
        pw.println("/**");
        pw.println(" * Source code generated by " + this.getClass().getName());
        pw.println(" */");
        pw.println("package " + packageName + ";");
        if (!imports.isEmpty()) {
            pw.println("import " + imports + ";");
        }
    }

    /**
     *
     * @param pw
     * @param packageName
     * @param className
     * @param implementations
     * @param extendedClassName
     */
    public void printClassDeclarationSerialVersionUID(PrintWriter pw,
            String packageName, String className, String implementations,
            String extendedClassName) {
        pw.print("public class " + className);
        if (!extendedClassName.isEmpty()) {
            pw.print(" extends " + extendedClassName + " {");
        }
        if (!implementations.isEmpty()) {
            pw.print(" implements " + implementations + " {");
        }
        pw.println();
        /**
         * This is not included for performance reasons. pw.println("private
         * static final long serialVersionUID = " + serialVersionUID + ";");
         */
    }

    /**
     * @param pw
     * @param fields
     * @param fieldTypes
     * @param v0
     */
    public void printFieldDeclarationsInitsAndGetters(PrintWriter pw,
            TreeSet<String> fields, HashMap<String, Integer> fieldTypes,
            HashMap<String, Byte> v0) {
        // Field declarations
        printFieldDeclarations(pw, fields, fieldTypes);
        // Field init
        printFieldInits(pw, fields, fieldTypes, v0);
        // Field getters
        printFieldGetters(pw, fields, fieldTypes);
    }

    /**
     * @param pw
     * @param fields
     * @param fieldTypes
     */
    public void printFieldDeclarations(PrintWriter pw, TreeSet<String> fields,
            HashMap<String, Integer> fieldTypes) {
        String field;
        int fieldType;
        Iterator<String> ite;
        ite = fields.iterator();
        while (ite.hasNext()) {
            field = ite.next();
            fieldType = fieldTypes.get(field);
            switch (fieldType) {
                case 0:
                    pw.println("protected String " + field + ";");
                    break;
                case 1:
                    pw.println("protected double " + field + ";");
                    break;
                case 2:
                    pw.println("protected int " + field + ";");
                    break;
                case 3:
                    pw.println("protected short " + field + ";");
                    break;
                case 4:
                    pw.println("protected byte " + field + ";");
                    break;
                default:
                    pw.println("protected boolean " + field + ";");
                    break;
            }
        }
    }

    /**
     *
     * @param pw
     * @param fields
     * @param fieldTypes
     */
    public void printFieldGetters(PrintWriter pw, TreeSet<String> fields,
            HashMap<String, Integer> fieldTypes) {
        Iterator<String> ite = fields.iterator();
        while (ite.hasNext()) {
            String field = ite.next();
            int fieldType = fieldTypes.get(field);
            switch (fieldType) {
                case 0:
                    pw.println("public String get" + field + "() {");
                    break;
                case 1:
                    pw.println("public double get" + field + "() {");
                    break;
                case 2:
                    pw.println("public int get" + field + "() {");
                    break;
                case 3:
                    pw.println("public short get" + field + "() {");
                    break;
                case 4:
                    pw.println("public byte get" + field + "() {");
                    break;
                default:
                    pw.println("public boolean get" + field + "() {");
                    break;
            }
            pw.println("return " + field + ";");
            pw.println("}");
            pw.println();
        }
    }

    /**
     *
     * @param pw
     * @param fields
     * @param fieldTypes
     * @param v0
     */
    public void printFieldInits(PrintWriter pw, TreeSet<String> fields,
            HashMap<String, Integer> fieldTypes, HashMap<String, Byte> v0) {
        Iterator<String> ite = fields.iterator();
        while (ite.hasNext()) {
            String field = ite.next();
            int fieldType = fieldTypes.get(field);
            switch (fieldType) {
                case 0:
                    pw.println("protected final void init" + field + "(String s) {");
                    pw.println("if (!s.trim().isEmpty()) {");
                    pw.println(field + " = s;");
                    break;
                case 1:
                    pw.println("protected final void init" + field + "(String s) {");
                    pw.println("if (!s.trim().isEmpty()) {");
                    pw.println(field + " = Double.parseDouble(s);");
                    pw.println("} else {");
                    pw.println(field + " = Double.NaN;");
                    break;
                case 2:
                    pw.println("protected final void init" + field + "(String s) {");
                    pw.println("if (!s.trim().isEmpty()) {");
                    pw.println(field + " = Integer.parseInt(s);");
                    pw.println("} else {");
                    pw.println(field + " = Integer.MIN_VALUE;");
                    break;
                case 3:
                    pw.println("protected final void init" + field + "(String s) {");
                    pw.println("if (!s.trim().isEmpty()) {");
                    pw.println(field + " = Short.parseShort(s);");
                    pw.println("} else {");
                    pw.println(field + " = Short.MIN_VALUE;");
                    break;
                case 4:
                    pw.println("protected final void init" + field + "(String s) {");
                    pw.println("if (!s.trim().isEmpty()) {");
                    pw.println(field + " = Byte.parseByte(s);");
                    pw.println("} else {");
                    pw.println(field + " = Byte.MIN_VALUE;");
                    break;
                default:
                    pw.println("protected final void init" + field + "(String s) {");
                    pw.println("if (!s.trim().isEmpty()) {");
                    pw.println("byte b = Byte.parseByte(s);");
                    if (v0.get(field) == null) {
                        pw.println(field + " = false;");
                    } else {
                        pw.println("if (b == " + v0.get(field) + ") {");
                        pw.println(field + " = false;");
                        pw.println("} else {");
                        pw.println(field + " = true;");
                        pw.println("}");
                    }
                    break;
            }
            pw.println("}");
            pw.println("}");
            pw.println();
        }
    }

    /**
     * Thinking to returns a lists of IDs...
     *
     * @param header
     * @param wave
     * @return
     */
    public String[] parseHeader(String header, int wave) {
        String[] r;
        String ws = "W" + wave;
        String keyIdentifier1 = "CASE" + ws;
        String keyIdentifier2 = "PERSON" + ws;
        String uniqueString1 = "uniqueString1";
        String uniqueString2 = "uniqueString2";
        String h1 = header.toUpperCase();
        try {
            if (h1.contains(uniqueString1)) {
                throw new Exception(uniqueString1 + " is not unique!");
            }
            if (h1.contains(uniqueString2)) {
                throw new Exception(uniqueString2 + " is not unique!");
            }
        } catch (Exception ex) {
            Logger.getLogger(ELSA_JavaCodeGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        h1 = h1.replaceAll("\t", " ,");
        h1 = h1 + " ";
        h1 = h1.replaceAll(keyIdentifier1, uniqueString1);
        h1 = h1.replaceAll(keyIdentifier2, uniqueString2);
        h1 = h1.replaceAll(ws + " ", " ");
        h1 = h1.replaceAll(" " + ws, " ");
        h1 = h1.replaceAll(ws + "_", "_");
        h1 = h1.replaceAll("_" + ws, "_");
        h1 = h1.replaceAll(ws + " ", "___" + ws + " ");
        h1 = h1.trim();
        h1 = h1.replaceAll(" ,", "\t");
        h1 = h1.replaceAll(uniqueString1, keyIdentifier1);
        h1 = h1.replaceAll(uniqueString2, keyIdentifier2);
        r = h1.split("\t");
        return r;
    }

    protected HashMap<String, Byte> setCommonBooleanMaps(
            HashMap<String, Byte>[] v0ms, HashMap<String, Byte>[] v1ms,
            TreeSet<String>[] allFields, HashMap<String, Integer> fieldTypes) {
        TreeSet<String> fields = allFields[5];
        HashMap<String, Byte> v0m1 = new HashMap<>();
        HashMap<String, Byte> v1m1 = new HashMap<>();
        Iterator<String> ites0 = fields.iterator();
        while (ites0.hasNext()) {
            String field0 = ites0.next();
            if (fieldTypes.get(field0) == 5) {
                for (int w = 0; w < v0ms.length; w++) {
                    HashMap<String, Byte> v0m = v0ms[w];
                    HashMap<String, Byte> v1m = v1ms[w];
                    Iterator<String> ites1 = v0m.keySet().iterator();
                    while (ites1.hasNext()) {
                        String field1 = ites1.next();
                        if (field0.equalsIgnoreCase(field1)) {
                            byte v0 = v0m.get(field1);
                            Byte v1;
                            if (v1m == null) {
                                v1 = Byte.MIN_VALUE;
                            } else {
                                //System.out.println("field1 " + field1);
                                //System.out.println("field1 " + field1);
                                v1 = v1m.get(field1);
                                if (v1 == null) {
                                    v1 = Byte.MIN_VALUE;
                                }
                            }
                            Byte v01 = v0m1.get(field1);
                            Byte v11 = v1m1.get(field1);
                            if (v01 == null) {
                                v0m1.put(field1, v0);
                            } else {
                                if (v01 != v0) {
                                    // Field better stored as a byte than boolean.
                                    fieldTypes.put(field1, 4);
                                }
                                if (v11 == null) {
                                    v1m1.put(field1, v1);
                                } else {
                                    if (v1 != v11.byteValue()) {
                                        // Field better stored as a byte than boolean.
                                        fieldTypes.put(field1, 4);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return v0m1;
    }

    /**
     * @param headers
     * @return
     */
    public TreeSet<String>[] getFields(String[][] headers) {
        int n = ELSA_Data.NWAVES;
        //System.out.println("n " + n);
        TreeSet<String>[] r = new TreeSet[n + 1];
        for (int i = 0; i < n; i++) {
            //System.out.println(i);
            r[i] = getFields(headers[i]);
        }
        r[n] = getFieldsInCommon(r);
        for (int i = 0; i < n; i++) {
            System.out.println(i);
            r[i].removeAll(r[n]);
        }
        return r;
    }

    /**
     * Finds and returns those fields that are in common and those fields .
     * result[0] are the fields in common with all.
     *
     * @param headers
     * @return
     */
    public ArrayList<String>[] getFieldsList(ArrayList<String> headers) {
        ArrayList<String>[] r;
        int size = headers.size();
        r = new ArrayList[size];
        Iterator<String> ite = headers.iterator();
        int i = 0;
        while (ite.hasNext()) {
            r[i] = getFieldsList(ite.next());
            i++;
        }
        return r;
    }

    /**
     *
     * @param fields
     * @return
     */
    public TreeSet<String> getFields(String[] fields) {
        TreeSet<String> r = new TreeSet<>();
        r.addAll(Arrays.asList(fields));
        return r;
    }

    /**
     *
     * @param s
     * @return
     */
    public ArrayList<String> getFieldsList(String s) {
        ArrayList<String> r = new ArrayList<>();
        String[] split = s.split("\t");
        r.addAll(Arrays.asList(split));
        return r;
    }

    /**
     * Returns all the values common to sets and removes all
     * these common fields from sets.
     *
     * @param sets
     * @return
     */
    public TreeSet<String> getFieldsInCommon(TreeSet<String>[] sets) {
        TreeSet<String> r = new TreeSet<>();
        r.addAll(sets[0]);
        for (int i = 1; i < sets.length - 1; i++) {
            System.out.println(i);
            r.retainAll(sets[i]);
        }
        return r;
    }
}
