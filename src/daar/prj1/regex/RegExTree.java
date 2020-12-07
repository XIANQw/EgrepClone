package daar.prj1.regex;

import java.util.ArrayList;

//UTILITARY CLASS
public class RegExTree {
    public int root;
    public ArrayList<RegExTree> subTrees;

    public RegExTree(int root, ArrayList<RegExTree> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }

    //FROM TREE TO PARENTHESIS
    public String toString() {
        if (subTrees.isEmpty()) return rootToString();
        StringBuilder result = new StringBuilder(rootToString() + "(" + subTrees.get(0).toString());
        for (int i = 1; i < subTrees.size(); i++) result.append(",").append(subTrees.get(i).toString());
        return result + ")";
    }

    public String rootToString() {
        if (root == RegEx.CONCAT) return ".";
        if (root == RegEx.ETOILE) return "*";
        if (root == RegEx.ALTERN) return "|";
        if (root == RegEx.DOT) return ".";
        return Character.toString((char) root);
    }

    public static String rootToString(int root) {
        if (root == RegEx.CONCAT) return ".";
        if (root == RegEx.ETOILE) return "*";
        if (root == RegEx.ALTERN) return "|";
        if (root == RegEx.DOT) return ".";
        if (root == RegEx.EPSILON) return "eps";
        return Character.toString((char) root);
    }
}
