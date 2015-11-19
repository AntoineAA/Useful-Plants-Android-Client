package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Trait {

    @SerializedName("name")
    private String name;

    @SerializedName("values")
    private List<String> values;

    public String getName() {
        return this.name;
    }

    public List<String> getValues() {
        return this.values;
    }

    public String getValuesAsString(String divider) {
        String string = "";
        if (this.values != null) {
            int count = 0;
            for (String s : this.values) {
                count++;
                string += s;
                if (this.values.size() != count) {
                    string += divider;
                }
            }
        }
        return string;
    }
}
