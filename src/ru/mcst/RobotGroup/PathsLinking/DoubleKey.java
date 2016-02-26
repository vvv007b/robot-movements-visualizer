package ru.mcst.RobotGroup.PathsLinking;

/**
 * Created by bocharov_n on 11.12.15.
 */
public class DoubleKey {
    private Object key1;
    private Object key2;

    public DoubleKey(Object key1, Object key2){
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public boolean equals(Object object){
        if (((DoubleKey)object).key1 == null && ((DoubleKey)object).key2 == null)
            return true;
        if (((DoubleKey)object).key1 == null && ((DoubleKey)object).key2.equals(this.key2))
            return true;
        if (((DoubleKey)object).key1.equals(this.key1) && ((DoubleKey)object).key2 == null)
            return true;
        if (((DoubleKey)object).key1.equals(this.key1) && ((DoubleKey)object).key2.equals(this.key2))
            return true;
        return false;
    }

    @Override
    public int hashCode(){
        int hashCode = this.key1 == null ? 0 : this.key1.hashCode();
        hashCode = hashCode + (this.key2 == null ? 0 : this.key2.hashCode());
        return hashCode;
    }

    public Object getKey1() {
        return key1;
    }

    public void setKey1(Object key1) {
        this.key1 = key1;
    }

    public Object getKey2() {
        return key2;
    }

    public void setKey2(Object key2) {
        this.key2 = key2;
    }
}
