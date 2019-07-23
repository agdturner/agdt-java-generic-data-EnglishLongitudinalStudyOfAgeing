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
import java.util.HashMap;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Environment;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Object;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Strings;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.io.ELSA_Files;

/**
 *
 * @author geoagdt
 */
public class ELSA_Data extends ELSA_Object {

    /**
     * Stores the number of waves in the ELSA
     */
    public static final byte NWAVES = 8;
    public static final byte W1 = 1;
    public static final byte W2 = 2;
    public static final byte W3 = 3;
    public static final byte W4 = 4;
    public static final byte W5 = 5;
    public static final byte W6 = 6;
    public static final byte W7 = 7;
    public static final byte W8 = 8;

    /**
     * Looks up from a CASEW1 to the Collection ID.
     */
    public HashMap<Short, Short> CASEW1ToCID;

    public ELSA_Data(ELSA_Environment we) {
        super(we);
        CASEW1ToCID = new HashMap<>();
    }

    /**
     * Caches and clears the first subset collection retrieved from an iterator.
     *
     * @return {@code true} iff a subset collection was cached and cleared.
     */
    public boolean clearSomeData() {
        return false;
    }

    /**
     * Caches and cleared all subset collections.
     *
     * @return The number of subset collections cached and cleared.
     */
    public int clearAllData() {
        int r = 0;
        return r;
    }

    /**
     * For caching a subset collection.
     *
     * @param cID the ID of subset collection to be cached.
     * @param o the subset collection to be cached.
     */
    public void cacheSubsetCollection(short cID, Object o) {
        cache(getELSASubsetCollectionFile(cID), o);
    }

    /**
     * For caching a subset collection.
     *
     * @param cID the ID of subset collection to be cached.
     * @param o the subset collection to be cached.
     */
    public void cacheSubsetCollectionSimple(short cID, Object o) {
        cache(getELSASubsetCollectionSimpleFile(cID), o);
    }

    /**
     * For loading a subset collection.
     *
     * @param cID the ID of subset collection to be loaded.
     * @return the subset collection loaded.
     */
    public Object loadSubsetCollection(short cID) {
        return load(getELSASubsetCollectionFile(cID));
    }

    /**
     * For loading a subset collection.
     *
     * @param cID the ID of subset collection to be loaded.
     * @return the subset collection loaded.
     */
    public Object loadSubsetCollectionSimple(short cID) {
        return load(getELSASubsetCollectionSimpleFile(cID));
    }

    /**
     * For getting a subset collection file.
     *
     * @param cID the ID of subset collection.
     * @return the subset collection file for cID.
     */
    public File getELSASubsetCollectionFile(short cID) {
        return new File(env.files.getGeneratedELSASubsetsDir(),
                ELSA_Strings.s_ELSA + ELSA_Strings.symbol_underscore + cID
                + env.files.DOT_DAT);
    }
    
    /**
     * For getting a subset collection simple file.
     *
     * @param cID the ID of subset collection.
     * @return the subset collection file for cID.
     */
    public File getELSASubsetCollectionSimpleFile(short cID) {
        return null;
//        return new File(env.files.getGeneratedELSASubsetsDir(),
//                ELSA_Strings.s_ELSA + ELSA_Strings.symbol_underscore
//                + ELSA_Strings.s_Simple + cID + ELSA_Files.DOT_DAT);
    }

    /**
     * Loads an Object from a File and reports this to the log.
     *
     * @param f the File to load an object from.
     * @return the object loaded.
     */
    protected Object load(File f) {
        String m = "load object from " + f.toString();
        env.logStartTag(m);
        Object r = env.ge.io.readObject(f);
        env.logEndTag(m);
        return r;
    }

    /**
     * Caches an Object to a File and reports this to the log.
     *
     * @param f the File to cache to.
     * @param o the Object to cache.
     */
    protected void cache(File f, Object o) {
        String m = "cache object to " + f.toString();
        env.logStartTag(m);
        env.ge.io.writeObject(o, f);
        env.logEndTag(m);
    }

}
