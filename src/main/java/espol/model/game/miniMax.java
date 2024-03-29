package espol.model.game;

import espol.model.tda.Heap;
import espol.model.tda.Tree;

import java.util.*;

public class miniMax {

    public static ArrayList<TreeMap<Integer, ArrayList<Cell>>> getNextMoves(TreeMap<Integer, ArrayList<Cell>> map, Character c) {
        ArrayList<TreeMap<Integer, ArrayList<Cell>>> result = new ArrayList<>();
        TreeMap<Integer, ArrayList<Cell>> tmp = Board.cloneMap(map);
        System.out.println("Get next Moves " + Board.countNulls(tmp));
        for (int i = 0; i<Board.countNulls(tmp); i++) {
            tmp = nextMove(tmp, c);
            result.add(tmp);
        }
        return result;
    }
    private static TreeMap<Integer, ArrayList<Cell>> nextMove(TreeMap<Integer, ArrayList<Cell>> map1, Character c) {
        TreeMap<Integer, ArrayList<Cell>> map = Board.cloneMap(map1);
        ArrayList<Pair> nulls = Board.getNullPairs(map);
        System.out.println("Nulls pair");
        nulls.forEach((v) -> {
            System.out.println("    " + v);
        });
        Pair currentNull;
        System.out.println("Nulls size: " + nulls.size());
        if (nulls.size() != 0) {
            currentNull = nulls.get(0);
            map.get(currentNull.x).get(currentNull.y).setC(c);
        }
        return map;
    }

    public static ArrayList<Pair> countNulls(TreeMap<Integer, ArrayList<Cell>> mapa){
        ArrayList<Pair> nulls = new ArrayList();

        for(Map.Entry<Integer, ArrayList<Cell>> par: mapa.entrySet()){
            for(Cell c:par.getValue()){
                if(c.getC().equals('n')){
                    nulls.add(c.getPosition());
                }
            }
        }

        return nulls;
    }


    public static Tree<Capsule> createTree(TreeMap<Integer, ArrayList<Cell>> map, Character c){
        Capsule cp = new Capsule(Board.cloneMap(map), c.equals('X') ? 'O':'X');
        Tree<Capsule> tmp = new Tree<>(cp);
        ArrayList<Pair> nulls = countNulls(map);
        if(!nulls.isEmpty()){
            for(int i=0; i<nulls.size(); i++){
                Pair position = nulls.get(i);
                TreeMap<Integer, ArrayList<Cell>> map1 = Board.cloneMap(map);
                map1.get(position.x).get(position.y).setC(c);
                map1.get(position.x).get(position.y).setSelected(true);
                Capsule cp1 = new Capsule(map1, c);
                Tree<Capsule> tmp1 = new Tree<>(cp1);
                tmp.addChild(tmp1);
                ArrayList<Pair> tmpNulls = countNulls(map1);
               System.out.println("Hijo "+i);
               printBoard(map1);
               System.out.println("--------------------------------------");
                for(int j=0; j<tmpNulls.size(); j++ ){
                    Pair position1 = tmpNulls.get(j);
                    TreeMap<Integer, ArrayList<Cell>> map2 = Board.cloneMap(map1);
                    map2.get(position1.x).get(position1.y).setC(c.equals('X') ? 'O':'X');
                    map2.get(position1.x).get(position1.y).setSelected(true);
                    Capsule cp2 = new Capsule(map2, c.equals('X') ? 'O':'X');
                    Tree<Capsule> tmp2 = new Tree<>(cp2);
                    tmp1.addChild(tmp2);
                   System.out.println("Nieto "+j);
                   printBoard(map2);
                   System.out.println("--------------------------------------");
                }
            }
        }
        setUtilities(tmp);
        setMax(tmp);
        return tmp;
    }

    public static void printBoard(TreeMap<Integer, ArrayList<Cell>> tablero){
        ArrayList<Cell> F0 = tablero.get(0);
        ArrayList<Cell> F1 = tablero.get(1);
        ArrayList<Cell> F2 = tablero.get(2);
        ArrayList<Character> F0P= new ArrayList<>();
        ArrayList<Character> F1P= new ArrayList<>();
        ArrayList<Character> F2P= new ArrayList<>();
        for(int i=0;i<F0.size();i++){
            if(F0.get(i).getC()=='n'){
                F0P.add('-');
            }
            else{
                Character tmpC=F0.get(i).getC();
                F0P.add(tmpC);
            }
        }
        for(int i=0;i<F1.size();i++){
            if(F1.get(i).getC()=='n'){
                F1P.add('-');
            }
            else{
                Character tmpC=F1.get(i).getC();
                F1P.add(tmpC);

            }
        }
        for(int i=0;i<F2.size();i++){
            if(F2.get(i).getC()=='n'){
                F2P.add('-');
            }
            else{
                Character tmpC=F2.get(i).getC();
                F2P.add(tmpC);

            }
        }

        System.out.println(F0P.get(0)+" "+ F0P.get(1)+" "+ F0P.get(2));
        System.out.println(F1P.get(0)+" "+ F1P.get(1)+" "+ F1P.get(2));
        System.out.println(F2P.get(0)+" "+ F2P.get(1)+" "+ F2P.get(2));
    }

    public static void setUtilities(Tree<Capsule> tree){
        for(Tree<Capsule> tree1:tree.getRoot().getChildren()){
            for(Tree<Capsule> tree2:tree1.getRoot().getChildren()){
                if(checkGame(tree2.getRoot().getContent().getC(),tree2.getRoot().getContent().getMap())){
                    tree2.getRoot().getContent().setUtility(-10);
                }else{
                    tree2.getRoot().getContent().setUtility(utilityFunction(tree2.getRoot().getContent().getMap(), tree2.getRoot().getContent().getC().equals('X') ? 'O':'X'));
                }
            }
        }
    }

    public static int utilityFunction(TreeMap<Integer, ArrayList<Cell>> tablero, Character c){
        int pPlayer=p(tablero,c);
        int pBot=p(tablero,c.equals('X') ? 'O':'X');
        return pPlayer-pBot;
    }

    public static int p(TreeMap<Integer, ArrayList<Cell>> tablero, Character c){
        int utilP=0;
        int filas=0;
        int columnas=0;
        int diagonales=0;
        ArrayList<Cell> F0 = tablero.get(0);
        ArrayList<Cell> F1 = tablero.get(1);
        ArrayList<Cell> F2 = tablero.get(2);
        //columnas
        for(int i=0;i<F0.size();i++){
            if((F0.get(i).getC()==c || F0.get(i).getC()=='n') && (F1.get(i).getC()==c || F1.get(i).getC()=='n') && (F2.get(i).getC()==c || F2.get(i).getC()=='n')){
                columnas++;
            }
        }
        //filas
        int tmp=0;
        for(Map.Entry<Integer, ArrayList<Cell>> par: tablero.entrySet()){  //comprobar si el caracter es el mismo o está vacío
            tmp=0;
            ArrayList<Cell> array=par.getValue();
            for(int i=0;i<array.size();i++){
                if(array.get(i).getC()==c || array.get(i).getC()=='n')
                    tmp++;
            }
            if(tmp==3){
                filas++;
            }
        }
        //diagonales
        if((F0.get(0).getC()==c || F0.get(0).getC()=='n') && (F1.get(1).getC()==c || F1.get(1).getC()=='n') && (F2.get(2).getC()==c || F2.get(2).getC()=='n')){
            diagonales++;
        }
        if((F0.get(2).getC()==c || F0.get(2).getC()=='n') && (F1.get(1).getC()==c || F1.get(1).getC()=='n') && (F2.get(0).getC()==c || F2.get(0).getC()=='n')){
            diagonales++;
        }
        utilP=filas+columnas+diagonales;
        return utilP;
    }

    public static void setMax(Tree<Capsule> tree){
        for(Tree<Capsule> tree1:tree.getRoot().getChildren()){
            if(checkGame(tree1.getRoot().getContent().getC(),tree1.getRoot().getContent().getMap())){
                tree1.getRoot().getContent().setMax(10);
            }else{
                tree1.getRoot().getContent().setMax(getMaxT(tree1));
            }
        }
    }

    public static int getMaxT(Tree<Capsule> tree){
        int max = 0;
        Comparator<Capsule> cmp = (Capsule i1, Capsule i2)-> {return i1.getUtility()-i2.getUtility();};
        Heap<Capsule> h = new Heap(cmp, false);
        for(Tree<Capsule> tree1:tree.getRoot().getChildren()){
           if(tree1.getRoot().getContent().getUtility()==-10){
               max=-10;
               return max;
           }else{
                h.insert(tree1.getRoot().getContent());
           }
        }
        if(!h.isEmpty()){max = h.remove().getUtility();}
        return max;
    }

    public static Capsule getMaxN(Tree<Capsule> tree){
        Capsule c = new Capsule();
        Comparator<Capsule> cmp = (Capsule i1, Capsule i2)-> i1.getMax()-i2.getMax();
        Heap<Capsule> h = new Heap(cmp, true);
        for(Tree<Capsule> tree1:tree.getRoot().getChildren()){
           if(tree1.getRoot().getContent().getMax()==10){
               c = tree1.getRoot().getContent();
               return c;
           }else{
               h.insert(tree1.getRoot().getContent());
           }
        }
        if(!h.isEmpty()){c= h.remove();}
        return c;
    }
    
        public static boolean checkGame(Character c, TreeMap<Integer, ArrayList<Cell>> tablero){
        ArrayList<Cell> F0 = tablero.get(0);
        ArrayList<Cell> F1 = tablero.get(1);
        ArrayList<Cell> F2 = tablero.get(2);
        //columnas
        for(int i=0;i<F0.size();i++){
            if((F0.get(i).getC()==c) && (F1.get(i).getC()==c) && (F2.get(i).getC()==c)){
                return true;
            }
        }
        //filas
        int tmp=0;
        for(Map.Entry<Integer, ArrayList<Cell>> par: tablero.entrySet()){  //comprobar si el caracter es el mismo o está vacío
            tmp=0;
            ArrayList<Cell> array=par.getValue();
            for(int i=0;i<array.size();i++){
                if(array.get(i).getC()==c)
                    tmp++;
            }
            if(tmp==3){
                return true;
            }
        }
        //diagonales
        if((F0.get(0).getC()==c ) && (F1.get(1).getC()==c) && (F2.get(2).getC()==c)){
            return true;
        }
        else if((F0.get(2).getC()==c ) && (F1.get(1).getC()==c ) && (F2.get(0).getC()==c)){
            return true;
        }
        return false;
    }
}
