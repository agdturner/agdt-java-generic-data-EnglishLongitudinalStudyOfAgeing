/*
 * Copyright 2018 Andy Turner, CCG, University of Leeds.
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
package uk.ac.leeds.ccg.andyt.generic.data.elsa.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_Environment;
//import uk.ac.leeds.ccg.andyt.data.postcode.Generic_UKPostcode_Handler;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.data.ELSA_Data;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.data.ELSA_Handler;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.io.ELSA_Files;

/**
 *
 * @author geoagdt
 */
public class ELSA_Environment extends ELSA_OutOfMemoryErrorHandler
        implements Serializable {

    public final transient Generic_Environment ge;
    public final transient ELSA_Files files;
    public final ELSA_Handler h;
    public final ELSA_Data data;
    public transient static final String EOL = System.getProperty("line.separator");

    /**
     * Stores the {@link ge} log ID for the log set up for WaAS.
     */
    public final int logID;

    public ELSA_Environment(File dataDir)throws IOException  {
        this(new Generic_Environment(dataDir), dataDir);
    }

    public ELSA_Environment(Generic_Environment ge) throws IOException {
        this(ge, ge.files.getDir());
    }

    public ELSA_Environment(Generic_Environment ge, File dataDir) throws IOException {
        //Memory_Threshold = 3000000000L;
        this.ge = ge;
        files = new ELSA_Files(dataDir);
        File f = files.getEnvDataFile();
        if (f.exists()) {
            data = (ELSA_Data) ge.io.readObject(f);
            initData();
            //data.env = this;
        } else {
            data = new ELSA_Data(this);
        }
        logID = ge.initLog(ELSA_Strings.s_ELSA);
        h = new ELSA_Handler(this);
    }

    private void initData() {
        data.env = this;
    }

    /**
     * A method to try to ensure there is enough memory to continue.
     *
     * @return
     */
    @Override
    public boolean checkAndMaybeFreeMemory() {
        System.gc();
        while (getTotalFreeMemory() < Memory_Threshold) {
//            int clear = clearAllData();
//            if (clear == 0) {
//                return false;
//            }
            if (!swapDataAny()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Attempts to swap some of {@link #data}.
     *
     * @param hoome handleOutOfMemoryError
     * @return {@code true} iff some data was successfully swapped.
     */
    @Override
    public boolean swapDataAny(boolean hoome) {
        try {
            boolean r = swapDataAny();
            checkAndMaybeFreeMemory();
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                boolean r = swapDataAny(HOOMEF);
                initMemoryReserve();
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap some of {@link #data}.
     *
     * @return {@code true} iff some data was successfully swapped.
     */
    @Override
    public boolean swapDataAny() {
        boolean r = clearSomeData();
        if (r) {
            return r;
        } else {
            System.out.println("No WaAS data to clear. Do some coding to try "
                    + "to arrange to clear something else if needs be. If the "
                    + "program fails then try providing more memory...");
            return r;
        }
    }

    /**
     * Attempts to clear some of {@link #data} using
     * {@link ELSA_Data#clearSomeData()}.
     *
     * @return {@code true} iff some data was successfully cleared.
     */
    public boolean clearSomeData() {
        return data.clearSomeData();
    }

    /**
     * Attempts to clear all of {@link #data} using
     * {@link ELSA_Data#clearAllData()}.
     *
     * @return The amount of data successfully cleared.
     */
    public int clearAllData() {
        int r = data.clearAllData();
        return r;
    }

    /**
     * For caching {@link #data} to {@link ELSA_Files#getEnvDataFile()}.
     */
    public void cacheData() {
        String m = "cacheData";
        logStartTag(m);
        ge.io.writeObject(data, files.getEnvDataFile());
        logEndTag(m);
    }

    /**
     * For convenience.
     * {@link Generic_Environment#logStartTag(java.lang.String, int)}
     *
     * @param s The tag name.
     */
    public final void logStartTag(String s) {
        ge.logStartTag(s, logID);
    }

    /**
     * For convenience. {@link Generic_Environment#log(java.lang.String, int)}
     *
     * @param s The message to be logged.
     */
    public void log(String s) {
        ge.log(s, logID);
    }

    /**
     * For convenience.
     * {@link Generic_Environment#logEndTag(java.lang.String, int)}
     *
     * @param s The tag name.
     */
    public final void logEndTag(String s) {
        ge.logEndTag(s, logID);
    }
}
