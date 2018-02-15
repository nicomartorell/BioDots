package com.nicocharm.biodots.screens;

import com.nicocharm.biodots.Antibiotic;

import java.util.Random;

public class ScreenCreator {

    private float initial_pOfDying;

    public float getInitial_pOfDying() {
        return initial_pOfDying;
    }

    public void setInitial_pOfDying(float initial_pOfDying) {
        this.initial_pOfDying = initial_pOfDying;
    }

    public short[] getButtonTypes() {
        return buttonTypes;
    }

    public void setButtonTypes(short[] buttonTypes) {
        this.buttonTypes = buttonTypes;
    }

    public float getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(float initialTime) {
        this.initialTime = initialTime;
    }

    public short[] getBacteriaTypes() {
        return types;
    }

    public void setBacteriaTypes(short[] types) {

        this.types = types;
    }

    private short[] buttonTypes;
    private float initialTime;
    private short[] types; //de acá saco n bacterias

    public boolean isFreeGame() {
        return isFreeGame;
    }

    public void setFreeGame(boolean freeGame) {
        isFreeGame = freeGame;
    }

    private boolean isFreeGame;

    public void setnBacterias(int nBacterias) {
        this.nBacterias = nBacterias;
        types = new short[nBacterias];
        Random random = new Random();
        for (int i = 0; i < nBacterias; i++) {
            types[i] = (short) (random.nextInt(5) + 1);
        }
    }

    private int nBacterias;

    public void setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
    }

    private int maxBlocks;

    public double getpOfRep() {
        return pOfRep;
    }

    public void setpOfRep(double pOfRep) {
        this.pOfRep = pOfRep;
    }

    private double pOfRep;

    public float getMutationStDev() {
        return mutationStDev;
    }

    private float mutationStDev;

    public ScreenCreator(){
        initial_pOfDying = 1.0f;

        buttonTypes = new short[5];
        buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
        buttonTypes[1] = Antibiotic.ANTIBIOTIC_BLUE;
        buttonTypes[2] = Antibiotic.ANTIBIOTIC_GREEN;
        buttonTypes[3] = Antibiotic.ANTIBIOTIC_PINK;
        buttonTypes[4] = Antibiotic.ANTIBIOTIC_RED;

        initialTime = 150f;

        nBacterias = 30;
        types = new short[nBacterias];
        Random random = new Random();
        for(int i = 0; i < nBacterias; i++){
            types[i] = (short)(random.nextInt(5) + 1);
        }

        isFreeGame = false;

        maxBlocks = 1;

        pOfRep = 0.0011;
        
        mutationStDev = 0.1f;
    }

    public int getMaxBlocks() {
        return maxBlocks;
    }

    public void setMutationStDev(float mutationStDev) {
        this.mutationStDev = mutationStDev;
    }
}
