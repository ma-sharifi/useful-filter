package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {
        String inputString = "Hello, World! ";
        // (2,8) (5,7) (2,4)  (1, 2);

        List<Pair> inputPairList = new ArrayList<>();
        inputPairList.add(new Pair(0, 13));
        inputPairList.add(new Pair(0, 12));
        inputPairList.add(new Pair(12, 13));
        inputPairList.add(new Pair(1, 12));

        Perantez[] perantezs = new Perantez[14];
        for (Pair pair : inputPairList) {
            Perantez PerantezClose= perantezs[pair.getClose()];
            Perantez PerantezOpen= perantezs[pair.getOpen()];
            if(PerantezClose==null)
                PerantezClose=new Perantez(0,0);
            if(PerantezOpen==null)
                PerantezOpen=new Perantez(0,0);
            if(PerantezClose!=null) {
                PerantezClose.setClose(PerantezClose.getClose() + 1);
            }
            if(PerantezOpen!=null)
                PerantezOpen.setOpen(PerantezOpen.getOpen()+1);
            perantezs[pair.getOpen()]=PerantezOpen;
            perantezs[pair.getClose()]=PerantezClose;
        }

        // (0,13) (0,12) (12,13) (1,12)
//        Perantez[] perantezs = {
//                new Perantez(2, 0),//0
//                new Perantez(1, 0),//1
//                null,//2
//                null,//3
//                null,//4
//                null,//5
//                null,//6
//                null,//7
//                null,//8
//                null,//9
//                null,//10
//                null,//11
//                new Perantez(1, 2),//12
//                new Perantez(0, 2)//13
//        };
//        Perantez[] perantezs = {
//                null,//0
//                new Perantez(1, 0),//1
//                new Perantez(2, 1),//2
//                null,//3
//                new Perantez(0, 1),//4
//                new Perantez(1, 0),//5
//                null,//6
//                new Perantez(0, 1),//7
//                new Perantez(0, 1),//8
//                null,//6
//                null,//6
//                null,//6
//                null,//6
//        };
        int added = 0;
        String result = "";
        for (int i = 0; i < inputString.length(); i++) {//TODO
            Perantez perantez = perantezs[i];
            if (perantez != null) {
                for (int j = 0; j < perantez.getClose(); j++) {
                    result += ")";
                    added++;
                }
                for (int j = 0; j < perantez.getOpen(); j++) {
                    result += "(";
                    added++;
                }
            }
            result += inputString.charAt(i);//OK
        }
        System.out.println("#result: " + Arrays.asList(result));
    }
    static class Pair {
        private int open = 0;
        private int close = 0;

        public Pair(int open, int close) {
            this.open = open;
            this.close = close;
        }



        public int getOpen() {
            return open;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "open=" + open +
                    ", close=" + close +
                    '}';
        }

        public void setOpen(int open) {
            this.open = open;
        }



        public int getClose() {
            return close;
        }

        public void setClose(int close) {
            this.close = close;
        }
    }

    static class Perantez {
        private int open = -1;
        private int close = -1;

        public Perantez(int open, int close) {
            this.open = open;
            this.close = close;
        }

        public int getOpen() {
            return open;
        }

        public int getClose() {
            return close;
        }

        public void setOpen(int open) {
            this.open = open;
        }

        public void setClose(int close) {
            this.close = close;
        }

        @Override
        public String toString() {
            return "Perantez{" +
                    "open=" + open +
                    ", close=" + close +
                    '}';
        }
    }
}
