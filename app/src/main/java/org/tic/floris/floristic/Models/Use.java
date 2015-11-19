package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Use {

    @SerializedName("usage")
    private String use;

    @SerializedName("names")
    private List<String> names;

    public String getUse() {
        return this.use;
    }

    public List<String> getNames() {
        return this.names;
    }

    public String getNamesAsString(String divider) {
        String string = "";
        if (this.names != null) {
            int count = 0;
            for (String s : this.names) {
                count++;
                string += s;
                if (this.names.size() != count) {
                    string += divider;
                }
            }
        }
        return string;
    }
}
