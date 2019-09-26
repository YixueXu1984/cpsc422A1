import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.lang.Math.abs;


public class Main {
    // create a table of map, 2d-vector, (x, y), save it's belief state value in the cell.
    static double[][] map = new double[4][3];
    static int counter;

    // fill the map with 0
    public static void initializeMap() {
        for (int x=0; x<4; x++) {
            for (int y=0;y<3;y++) {
                map[x][y] =0.0;
            }
        }
        counter = 0;
    }

    private static void initializeBS(boolean knowS0, int a, int b) {
        if (!knowS0) {
            // every cell's initial state is 0.111
            for (int x=0; x<4;x++){
                for (int y=0;y<3;y++) {
                    map[x][y] = 0.111;
                }
            }
            map[3][1] = 0;
            map[3][2] = 0;
            map[1][1] = -1;
        }
     else {
            // if S0 is known, the bs for s0 is one, and every other cell is 0
            initializBSwithS0(a,b);
        }
    }

    private static void initializBSwithS0(int a, int b){

        map[a][b] = 1;
        map[1][1] = -1;
    }

    private static void updateBS(String move, int obs) {
        counter++;

        //1st step:
        //make a copy of the original belief state for updating purpose
        double[][] localmap = new double[4][3];
        for (int x=0; x<4; x++) {
            for (int y=0;y<3;y++) {
                localmap[x][y] =map[x][y];
            }
        }

        //2nd step: update the belief state value of each cell
        //b'(x,y) = 1/alpha * P(e|(x,y)) * sum((P((x,y)|move, (x0,y0))*b(xn,yn))
        double alpha = (double) 1/counter;
        for (int x=0; x<4; x++) {
            for (int y=0;y<3;y++) {
                map[x][y] = (alpha) * (tellPobs(obs,x,y)) * whatsInSum(move, x,y, localmap);
            }
        }

        //todo: deal with (1,1)special case
    }

    private static double whatsInSum(String move, int x, int y, double[][] localmap) {
        //todo
        // every new state can only come from 5 possible original state:
        // from 1 cell up,
        // from 1 cell down,
        // from 1 cell left,
        // from 1 cell right,
        // from original place(hit the wall)

        // 4 command: up, down, left, right
        // for every command, check 5 possible cases for s'
        double a = 0;
        if (move.equals("up")) {
            a =  sumPUp(x,y,localmap);
        }
        else if (move.equals("down")) {
            a =  sumPDown(x,y,localmap);
        }
        else if (move.equals("left")) {
            a =   sumPLeft(x,y,localmap);
        }
        else if (move.equals("right")) {
            a = sumPRight(x,y,localmap);
        }
        //stub
        return a;
    }

    private static double sumPUp(int x, int y, double[][] localmap) {
        //todo: stub

        double sum = 0;
        // case 1: previous state is from left cell, P=0.1
        //[ x =0 | x =1 | x =2 | x =3 ]
        //[ N    | Y    | Y    | Y    ]  y=2
        //[ N    | NULL | N    | Y    ]  y=1
        //[ N    | Y    | Y    | Y    ]  y=0
        // following if statement is defined by " if is Y, add to sum"
        if ((x==1&&y==0) || (x==2&&y==0) || (x==3&&y==0) || (x==3&&y==1)||(x==1&&y==2) || (x==2&&y==2) ||(x==3&&y==2)) {
            sum = sum + (0.1 * localmap[x-1][y]);
        }

        // case 2: previous state is from right cell, P=0.1
        //[ x =0 | x =1 | x =2 | x =3 ]
        //[ Y    | Y    | N    | N    ]  y=2
        //[ N    | NULL | N    | N    ]  y=1
        //[ Y    | Y    | Y    | N    ]  y=0
        if ((x==0&&y==0) || (x==1&&y==0) || (x==2&&y==0) || (x==0&&y==2) || (x==1&&y==2)) {
            sum = sum + (0.1 * localmap[x+1][y]);
        }

        // case 3: previous state is from upper cell, P = 0.1
        //[ x =0 | x =1 | x =2 | x =3 ]
        //[ N    | N    | N    | N    ]  y=2
        //[ Y    | NULL | Y    | N    ]  y=1
        //[ Y    | N    | Y    | N    ]  y=0
        if ((x==0&&y==0) || (x==0&&y==1) || (x==2&&y==0) || (x==2&&y==1)) {
            sum = sum + (0.1 * localmap[x][y+1]);
        }

        //case 4: previous state is from lower cell, p = 0.8
        //[ x =0 | x =1 | x =2 | x =3 ]
        //[ Y    | N    | Y    | N    ]  y=2
        //[ Y    | NULL | Y    | Y    ]  y=1
        //[ N    | N    | N    | N    ]  y=0
        if ((x==0&&y==1) || (x==0&&y==2) || (x==2&&y==1) || (x==2&&y==2) || (x==3&&y==1)) {
            sum = sum + (0.8 * localmap[x][y-1]);
        }

        //case 5: hit the wall and stayed in same place when command is up
        //[ x =0 | x =1 | x =2 | x =3 ]
        //[ 0.8  | 0.8  | 0.7  | N    ]  y=2
        //[ 0.1  | NULL | 0.1  | N    ]  y=1
        //[ 0.2  | 0.8  | 0.1  | 0.2  ]  y=0
        if ((x==0 && y==1) || (x==2&&y==0) || (x==2&&y==1)) {
            sum = sum + (0.1 * localmap[x][y]);
        }
        if ((x==0 && y==0) || (x==3&&y==0)) {
            sum = sum + (0.2 * localmap[x][y]);
        }
        if (x==2&&y==2) {
            sum = sum + (0.7 * localmap[x][y]);
        }
        if ((x==0&&y==2) || (x==1&&y==0) || (x==1&&y==2)) {
            sum = sum + (0.8 * localmap[x][y]);
        }

        return sum;
    }

    private static double sumPDown(int x, int y, double[][] localmap) {
        //todo: stub
        return 0;
    }

    private static double sumPLeft(int x, int y, double[][] localmap) {
        //todo: stub
        return 0;
    }

    private static double sumPRight(int x, int y, double[][] localmap) {
        //todo: stub
        return 0;
    }


    //if in first 2 column, P(2w) = 0.9, P(1w) = 0.1, P(end) = 0
    //in the non-terminal 3rd column, P(2w) = 0.1, P(1w) = 0.9, P(end) = 0
    // in the terminal 3rd cell, P(2w) = 0, P(1w) = 0, P(end) = 1
    private static double tellPobs(int obs, int x, int y) {

        if (x == 2) {
            if (obs == 2) {
                return 0.1;
            }
            else if (obs ==1) {
                return 0.9;
            } else return 0;
        }
        else if (x == 3 && (y == 2|| y == 1)) {
            if (obs == 0) {
                return 1;
            } else return 0;
        }

        else {
            if (obs == 2) {
                return 0.9;
            }
            else if (obs == 1) {
                return 0.1;
            }
            else return 0;
        }
    }

    public static void main(String[] args) {

        //very informal test case
        initializeMap();
        initializeBS(false,0,0);
        updateBS("up",2);
        updateBS("up",2);
        updateBS("up",2);
        for (int y=2; y>=0; y--) {
            for (int x = 0; x < 4; x++) {
                System.out.print("(" +x + "," + y +")"+ ": "+map[x][y] + "  ");
            }
            System.out.println("");
        }
        System.out.println(counter);
    }


}
