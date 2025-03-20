package com.example;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;


public class MyBinaryTree {


    /**
     * 二叉树节点
     */
    public static class TreeNode {
        int data;
        TreeNode leftChild;
        TreeNode rightChild;

        TreeNode(int data) {
            this.data = data;
        }
    }

    /*-------------↓↓↓----------------深度遍历----------------------↓↓↓------------------------*/
    /**
     * 构建二叉树
     *
     * @param inputList 输入序列
     * @return TreeNode
     */
    private static TreeNode createBinaryTree(LinkedList<Integer> inputList) {
        TreeNode node = null;
        if (inputList == null || inputList.isEmpty()) {
            return null;
        }
        Integer data = inputList.removeFirst();
        if (data != null) {
            node = new TreeNode(data);
            node.leftChild = createBinaryTree(inputList);
            node.rightChild = createBinaryTree(inputList);
        }
        return node;
    }

    /**
     * 二叉树前序遍历
     *
     * @param node 二叉树节点
     */
    private static void preOrderTraversal(TreeNode node) {
        if (node == null) {
            return;
        }
        System.out.println(node.data);
        preOrderTraversal(node.leftChild);
        preOrderTraversal(node.rightChild);
    }

    /**
     * 二叉树中序遍历
     *
     * @param node 二叉树节点
     */
    private static void inOrderTraversal(TreeNode node) {
        if (node == null) {
            return;
        }
        inOrderTraversal(node.leftChild);
        System.out.println(node.data);
        inOrderTraversal(node.rightChild);
    }

    /**
     * 二叉树后序遍历
     *
     * @param node 二叉树节点
     */
    private static void postOrderTraversal(TreeNode node) {
        if (node == null) {
            return;
        }
        postOrderTraversal(node.leftChild);
        postOrderTraversal(node.rightChild);
        System.out.println(node.data);
    }



    /*-------------↓↓↓----------------广度遍历-------------------------↓↓↓---------------------*/

    /**
     * 通过队列实现二叉树层序遍历
     * @param root 二叉树根节点
     */
    private static void levelTraversal(TreeNode root){
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()){
            TreeNode node = queue.poll();
            System.out.println(node.data);
            if (node.leftChild != null){
                queue.offer(node.leftChild);
            }
            if (node.rightChild != null){
                queue.offer(node.rightChild);
            }
        }
    }
    public static void main(String[] args) {
        Integer[] integers = new Integer[]{3, 2, 9, null, null, 10, null, null, 8, null, 4};
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(integers));
        TreeNode treeNode = createBinaryTree(list);

        System.out.println("前序遍历");
        preOrderTraversal(treeNode);
        System.out.println("中序遍历");
        inOrderTraversal(treeNode);
        System.out.println("后序遍历");
        postOrderTraversal(treeNode);

        System.out.println("层序遍历");
        levelTraversal(treeNode);
    }
}
