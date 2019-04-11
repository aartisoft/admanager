package com.admanager.boosternotification.battery;

public class BatteryStatus {
    private int level;
    private float temp;
    private float voltage;

    public BatteryStatus() {
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BatteryStatus)) return false;

        BatteryStatus that = (BatteryStatus) o;

        if (level != that.level) return false;
        if (Float.compare(that.temp, temp) != 0) return false;
        return Float.compare(that.voltage, voltage) == 0;
    }

    @Override
    public int hashCode() {
        int result = level;
        result = 31 * result + (temp != +0.0f ? Float.floatToIntBits(temp) : 0);
        result = 31 * result + (voltage != +0.0f ? Float.floatToIntBits(voltage) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BatteryStatus{" +
                "level=" + level +
                ", temp=" + temp +
                ", voltage=" + voltage +
                '}';
    }

    public CharSequence getStatusString() {
        // two time %% is for addding percent sign in string format
        return String.format("%s %%", level);
    }
}
