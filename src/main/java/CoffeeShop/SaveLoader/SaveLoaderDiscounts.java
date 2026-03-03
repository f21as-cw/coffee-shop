package CoffeeShop.SaveLoader;

import CoffeeShop.Customer;
import CoffeeShop.Discounts.IDiscount;
import CoffeeShop.Item;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SaveLoaderDiscounts extends ASaveLoader<IDiscount>{

    public SaveLoaderDiscounts(String readPath, String writePath) {
        super(readPath, writePath);
    }

    @Override
    IDiscount StringToEntity(String str) {
        try {
            String[] parts = str.split(",", 3);
            String className = parts[0];
            UUID id = UUID.fromString(parts[1]);
            String[] rawVars = parts[2].replace("(", "").replace(")", "").split(":");

            if (parts.length < 3) {
                // There is missing information
                return null;
            }

            Class<?> c = Class.forName(className);
            if (c.getConstructors().length > 1)
                throw new Exception("DISCOUNTS CAN ONLY SHOULD ONLY HAVE A SINGLE CONSTRUCTOR");

            Constructor<?> constructor = c.getConstructors()[0];
            Parameter[] params = constructor.getParameters();
            Object[] args = new Object[params.length];

            for (int i = 0; i < params.length; i++){
                String val = rawVars[i].trim();
                Class<?> type = params[i].getType();

                //this works for now
                try {
                    if (type == int.class || type == Integer.class) args[i] = Integer.parseInt(val);
                    else if (type == double.class || type == Double.class) args[i] = Double.parseDouble(val);
                    else if (type == float.class || type == Float.class) args[i] = Float.parseFloat(val);
                    else if (type == Item.class) args[i] = new Item(val);
                    else if (type == List.class){
                        String[] list = val.trim().split(";");
                        List<Item> items = new ArrayList<>();
                        for (String s : list) {
                            items.add(new Item(s));
                        }
                        args[i] = items;
                    }
                    else args[i] = val;
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Discount loading error");
                }

            }

            return (IDiscount) constructor.newInstance(args);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    String EntityToString(IDiscount entity) {
        return entity.EntityToString();
    }
}
