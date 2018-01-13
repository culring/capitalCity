package application.testGenerator;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Culring on 2018-01-09.
 */
class Tree{
    private TreeNode root;
    private int nodeLabel = 0;

    Tree(int treeSize){
        root = generateSubtree(treeSize);
    }

    @Override
    public String toString(){
        List<String> tree = root.inOrderSearch();
        return tree.toString();
    }

    List<List<Integer>> getConnections(){
        return root.getConnections();
    }

    int getRoot(){
        return root.getValue();
    }

    private TreeNode generateSubtree(int subtreeSize){
        if(subtreeSize == 0){
            return null;
        }

        Random rand = new Random();
        int leftTreeSize = rand.nextInt((subtreeSize+2)/2);
        int rightTreeSize = (subtreeSize-1)-leftTreeSize;
        TreeNode leftTree = generateSubtree(leftTreeSize);
        //int label = labels.remove();
        TreeNode rightTree = generateSubtree(rightTreeSize);
        TreeNode root = new TreeNode(nodeLabel++);
        root.left = leftTree;
        root.right = rightTree;

        return root;
    }

    private class TreeNode{
        private int value;
        private TreeNode left, right;

        TreeNode(int nodeNumber){
            value = nodeNumber;
            left = null;
            right = null;
        }

        List<String> inOrderSearch(){
            List<String> tree = new LinkedList<>();

            if(left != null){
                tree.add("L");
                tree.addAll(left.inOrderSearch());
            }
            tree.add("X");
            if(right != null) {
                tree.add("R");
                tree.addAll(right.inOrderSearch());
            }
            return tree;
        }

        List<List<Integer>> getConnections(){
            List<List<Integer>> tree = new LinkedList<>();

            class Connection{
                void addConnection(int node1, int node2){
                    List<Integer> list = new LinkedList<>();
                    list.add(node1);
                    list.add(node2);
                    tree.add(list);
                }
            }

            Connection connection = new Connection();
            if(left != null){
                connection.addConnection(value, left.value);
                tree.addAll(left.getConnections());
            }
            if(right != null) {
                connection.addConnection(value, right.value);
                tree.addAll(right.getConnections());
            }
            return tree;
        }

        private TreeNode getLeft() {
            return left;
        }

        private TreeNode getRight() {
            return right;
        }

        int getValue() {
            return value;
        }
    }
}