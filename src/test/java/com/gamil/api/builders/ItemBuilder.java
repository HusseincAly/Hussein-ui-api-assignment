package com.gamil.api.builders;
import java.util.*;
// Builder: valid defaults, overridden per scenario.
public final class ItemBuilder {
 private String name="TrainingItem "+UUID.randomUUID().toString().replace("-","");
 private double price=12.50; private int stock=10;
 public ItemBuilder price(double value) { price=value; return this; }
 public ItemBuilder stock(int value) { stock=value; return this; }
 public Map<String,Object> build() { return new LinkedHashMap<>(Map.of("name",name,"description","Assignment test product","price",price,"stock",stock)); }
}
