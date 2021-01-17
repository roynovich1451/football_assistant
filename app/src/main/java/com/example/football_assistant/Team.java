package com.example.football_assistant;

public class Team implements Comparable{

    private String name, wins, draws, losses, GF, GA, points;

    public Team() {
        this.name = "";
        this.wins = "0";
        this.draws = "0";
        this.losses = "0";
        this.GF = "0";
        this.GA = "0";
        this.points = "0";
    }

    public Team(String name, String wins, String draws, String losses, String GF, String GA, String points) {
        this.name = name;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.GF = GF;
        this.GA = GA;
        this.points = points;
    }

    public void updateAfterGame(String GF, String GA) {
        String result;
        int iGF = Integer.parseInt(GF);
        int iGA = Integer.parseInt(GA);

        if (iGF > iGA) result = "win";
        else if (iGF < iGA) result = "loose";
        else result = "draw";

        switch (result){
            case "win":
                addWin();
                break;
            case "draw":
                addDraw();
                break;
            case "loose":
                addLoose();
                break;
        }
        updateScore(iGF, iGA);
    }

    public void updateScore(int GF, int GA){
        this.GF = String.valueOf(Integer.parseInt(this.GF) + GF);
        this.GA = String.valueOf(Integer.parseInt(this.GA) + GA);
    }

    public void addWin(){
        this.wins = String.valueOf(Integer.parseInt(this.wins) + 1);
        this.points = String.valueOf(Integer.parseInt(this.points) + 3);
    }

    public void addDraw(){
        this.draws = String.valueOf(Integer.parseInt(this.draws) + 1);
        this.points = String.valueOf(Integer.parseInt(this.points) + 1);
    }

    public void addLoose(){
        this.losses = String.valueOf(Integer.parseInt(this.losses) + 1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWins() {
        return wins;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }

    public String getDraws() {
        return draws;
    }

    public void setDraws(String draws) {
        this.draws = draws;
    }

    public String getLosses() {
        return losses;
    }

    public void setLosses(String losses) {
        this.losses = losses;
    }

    public String getGF() {
        return GF;
    }

    public void setGF(String GF) {
        this.GF = GF;
    }

    public String getGA() {
        return GA;
    }

    public void setGA(String GA) {
        this.GA = GA;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    @Override
    public int compareTo(Object o) {
        if (Integer.parseInt(this.points) - Integer.parseInt(((Team)o).getPoints()) == 0){
            int this_GD = Integer.parseInt(this.getGF()) - Integer.parseInt(this.getGA());
            int other_GD = Integer.parseInt(((Team)o).getGF()) - Integer.parseInt(((Team)o).getGA());
            if (this_GD - other_GD == 0){
                int this_GF = Integer.parseInt(this.getGF());
                int other_GF = Integer.parseInt(((Team)o).getGF());
                return -1 * (this_GF - other_GF);
            }
            else{
                return -1 * (this_GD - other_GD);
            }
        }
        else {
            return -1 * (Integer.parseInt(this.points) - Integer.parseInt(((Team) o).getPoints()));
        }
    }
}
