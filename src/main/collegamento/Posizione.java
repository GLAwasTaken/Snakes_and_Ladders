package main.collegamento;

public class Posizione {
    int x,y;

    public Posizione(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Posizione)) {
            return false;
        }
        Posizione p = (Posizione) obj;
        return this.getX()==p.getX() && this.getY()==p.getY();
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
}
