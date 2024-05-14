package com.daxton.rolemaster.type;

import com.daxton.rolemaster.RoleMaster;
import org.bukkit.entity.*;

public enum DamageType {

    //近戰Melee 遠距離Range 魔法Magic
    MELEE, RANGE, MAGIC,
    //近戰倍率MeleeMultiply 遠距離倍率RangeMultiply 魔法倍率MagicMultiply
    MELEEMULTIPLY, RANGEMULTIPLY, MAGICMULTIPLY,
    //近戰增加MeleeAdd 遠距離增加RangeAdd 魔法增加MagicAdd
    MELEEADD, RANGEADD, MAGICADD;

    public static DamageType getDamageType(String typeName){
        try {
            return DamageType.valueOf(typeName.toUpperCase());
        }catch (IllegalArgumentException e) {
            return MELEE;
        }
    }

//    public static DamageType getDamageType(String typeName){
//        // 将输入字符串转换为大写，以便不区分大小写
//        typeName = typeName.trim().toUpperCase();
//
//        switch (typeName) {
//            case "MElEE":
//            case "RANGE":
//            case "MAGIC":
//                return DamageType.valueOf(typeName);
//            case "MElEE_MULTIPLY":
//                return MElEE_MULTIPLY;
//            case "RANGE_MULTIPLY":
//                return RANGE_MULTIPLY;
//            case "MAGIC_MULTIPLY":
//                return MAGIC_MULTIPLY;
//            case "MElEE_ADD":
//                return MElEE_ADD;
//            case "RANGE_ADD":
//                return RANGE_ADD;
//            case "MAGIC_ADD":
//                return MAGIC_ADD;
//            default:
//                RoleMaster.sendLogger(typeName + " 錯誤!!!!!!!!!");
//                return MElEE;
//        }
//    }

    public static DamageType getDamageType(double number){
        String damageNumber = String.valueOf(number);
        if(damageNumber.contains(".3444")){return RANGEMULTIPLY;}
        if(damageNumber.contains(".3333")){return RANGEADD;}
        if(damageNumber.contains(".3222")){return RANGE;}
        if(damageNumber.contains(".2444")){return MAGICMULTIPLY;}
        if(damageNumber.contains(".2333")){return MAGICADD;}
        if(damageNumber.contains(".2222")){return MAGIC;}
        if(damageNumber.contains(".1444")){return MELEEMULTIPLY;}
        if(damageNumber.contains(".1333")){return MELEEADD;}
        return MELEE;
    }

    public static DamageType getDamageType(Entity entity, double number){
        DamageType damageType = getDamageType(number);
        if(damageType == MELEE){return getDamageType(entity);}
        return damageType;
    }

    public static DamageType getDamageType(Entity entity){

        if(entity instanceof Arrow){
            if(((Arrow) entity).getShooter() instanceof Player){
                return RANGE;
            }
        }
        if(entity instanceof ThrownPotion){
            if(((ThrownPotion) entity).getShooter() instanceof Player){
                return MAGIC;
            }
        }
        if(entity instanceof TNTPrimed){
            if(((TNTPrimed) entity).getSource() instanceof Player){
                return MAGIC;
            }
        }
        if(entity instanceof Projectile){
            if(((Projectile) entity).getShooter() instanceof Player){
                return MAGIC;
            }
        }
        if(entity instanceof Fireball){
            if(((Fireball) entity).getShooter() instanceof Player){
                return MAGIC;
            }
        }

        return MELEE;
    }

}
