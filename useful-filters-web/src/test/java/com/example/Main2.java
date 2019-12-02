package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main2 {

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

        String result = String.format("%20s"," ");
        System.out.println("result:["+result+"]");
        for (int i = 0; i < inputString.length(); i++) {//TODO
            result="";
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
