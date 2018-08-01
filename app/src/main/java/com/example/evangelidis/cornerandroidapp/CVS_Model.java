package com.example.evangelidis.cornerandroidapp;

public class CVS_Model {
    private String time;
    private int punch_ty;
    private double speed_m;
    private double power_g;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPunch_ty() {
        return punch_ty;
    }

    public void setPunch_ty(int punch_ty) {
        this.punch_ty = punch_ty;
    }

    public double getSpeed_m() {
        return speed_m;
    }

    public void setSpeed_m(double speed_m) {
        this.speed_m = speed_m;
    }

    public double getPower_g() {
        return power_g;
    }

    public void setPower_g(double power_g) {
        this.power_g = power_g;
    }

    @Override
    public String toString() {
        return "BoxVariable{" +
                "time='" + time + '\'' +
                ", punch_ty='" + punch_ty + '\'' +
                ", speed_m='" + speed_m + '\'' +
                ", power_g='" + power_g + '\'' +
                '}';
    }
}


