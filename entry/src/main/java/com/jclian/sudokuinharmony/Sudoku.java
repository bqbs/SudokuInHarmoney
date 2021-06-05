package com.jclian.sudokuinharmony;


import java.util.*;
import java.util.function.Function;

/**
 * 该算法根据知乎大神写的生成算法详解
 * https://zhuanlan.zhihu.com/p/67447747
 * 数独的求解用上了Dance Link X算法，知乎大神也做了解释
 * https://zhuanlan.zhihu.com/p/67324277
 *
 * 下面使用kotlin，转写了一下
 */
public class Sudoku {


    public static Map<String, Integer> initLocationDict(int initCount) {
        Map<String, Integer> dict = new HashMap<String, Integer>();
        Set<Integer> s = new HashSet<Integer>();
        while (dict.values().size() < initCount) {
            int i = (int) (Math.random() * 9);
            int j = (int) (Math.random() * 9);
            int k = 1 + (int) (Math.random() * 9);

            Integer a = i * 9 + j;
            if (s.contains(a)) {
                continue;
            }
            Integer b = i * 9 + k + 80;
            if (s.contains(b)) {
                continue;
            }
            Integer c = j * 9 + k + 161;
            if (s.contains(c)) {
                continue;
            }
            Integer d = ((i / 3) * 3 + (j / 3)) * 9 + k + 242;
            if (s.contains(d)) {
                continue;
            }
            s.add(a);
            s.add(b);
            s.add(c);
            s.add(d);
            dict.put(i + "," + j, k);
        }
        return dict;
    }

    int[][] getFormattedAnswer(List<String> ans) {
        ans.sort(String::compareTo);
        int[][] arr = new int[9][9];
        for (String row : ans) {
            int row_id = Integer.parseInt(row);
            int loc = row_id / 9;
            int i = (loc / 9);
            int j = loc % 9;
            int k = row_id % 9 + 1;
            arr[i][j] = k;
        }
        return arr;
    }

    static Map<String, Integer> getSudokuMap(List<String> ans) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        ans.sort(String::compareTo);
        int[][] arr = new int[9][9];
        for (String row : ans) {
            int row_id = Integer.parseInt(row);

            int loc = row_id / 9;
            int i = (loc / 9);
            int j = loc % 9;
            int k = row_id % 9 + 1;
            map.put(i + "," + j, k);
        }

        // 随机挖空
        for (int count = 0; count <= 30; count++) {
            int i = (int) (Math.random() * 9);
            int j = (int) (Math.random() * 9);
            String key = i + "," + j;
            map.remove(key);
        }

        return map;
    }

    static CrossCycleLinkNode<String> getSudokuLinkedList(Map<String, Integer> map) {
        CrossCycleLinkNode<String> head = initCol(324);
        for (int i = 0; i <= 8; i++) {
            for (int j = 0; j <= 8; j++) {
                String key = i + "," + j;
                if (map.containsKey(key)) {

                    Integer k = map.get(key);
                    // 条件一：max 8×9+8
                    int a = i * 9 + j;
                    // 所以这里加 80,实际上共81个条件
                    //
                    int b = i * 9 + k + 80;
                    int c = j * 9 + k + 161;
                    int d = ((i / 3) * 3 + (j / 3)) * 9 + k + 242;
                    int rowId = (i * 9 + j) * 9 + k - 1;
                    String[] strArr = new String[]{String.valueOf(a), String.valueOf(b), String.valueOf(c), String.valueOf(d)};
                    appendRow(head, String.valueOf(rowId), Arrays.asList(strArr));

                } else {

                    for (int k = 1;k<=9;k++){
                        int a = i * 9 + j;
                        int b = i * 9 + k + 80;
                        int c = j * 9 + k + 161;
                        int d = ((i / 3) * 3 + (j / 3)) * 9 + k + 242;
                        int rowId = (i * 9 + j) * 9 + k - 1;
                        String[] strArr = new String[]{String.valueOf(a), String.valueOf(b), String.valueOf(c), String.valueOf(d)};
                        appendRow(head, String.valueOf(rowId), Arrays.asList(strArr));
                    }
                }
            }

        }
        return head;
    }

    static CrossCycleLinkNode<String> initCol(int col_count) {
        CrossCycleLinkNode<String> head = new CrossCycleLinkNode<String>("head", "column");
        for (int i = 0; i < col_count; i++) {
            CrossCycleLinkNode<String> colNode = new CrossCycleLinkNode<String>(String.valueOf(i), head.mRow);
            colNode.right = head;
            colNode.left = head.left;
            colNode.right.left = colNode;
            colNode.left.right = colNode;
        }
        return head;
    }


    static void appendRow(CrossCycleLinkNode<String> head, String row_id, List<String> list) {

        CrossCycleLinkNode<String> last = null;
        CrossCycleLinkNode<String> col = head.right;
        for (String num : list) {
            while (!col .equals(head)) {
                if (col.mValue .equals(num) ) {
                    CrossCycleLinkNode<String> node = new CrossCycleLinkNode<>(String.valueOf(1), row_id);
                    node.col = col;
                    node.down = col;
                    node.up = col.up;
                    node.down.up = node;
                    node.up.down = node;
                    if (last != null) {
                        node.left = last;
                        node.right = last.right;
                        node.left.right = node;
                        node.right.left = node;

                    }
                    last = node;
                    break;
                }

                col = col.right;
            }

        }

    }

    static boolean danceLinkX(CrossCycleLinkNode<String> head, List<String> answers) {
        if (head.right .equals(head) ) {
            return true;
        }

        CrossCycleLinkNode<String> node = head.right;
        while (!node .equals(head) ) {
            if (node.down == node ) {
                return false;
            }
            node = node.right;
        }

        List<Runnable> restores = new ArrayList < > ();
        CrossCycleLinkNode<String>  firstCol = head.right;
        firstCol.removeColumn();
        Runnable restoreColumn = firstCol::restoreColumn;
        restores.add(restoreColumn);

        node = firstCol.down;
        while (!node.equals(firstCol) ) {
            if (!node.right .equals(node)) {
                node.right.removeRow();
                restores.add(node.right::restoreRow);
            }
            node = node.down;
        }
        int curRestoresCount = restores.size() ;
        CrossCycleLinkNode<String> selectedRow = firstCol.down;
        while (!selectedRow .equals(firstCol)) {
            answers.add(selectedRow.mRow);
            if (!selectedRow.right .equals(selectedRow)) {
                CrossCycleLinkNode<String> rowNode = selectedRow.right;
                while (true) {
                    CrossCycleLinkNode<String> colNode = rowNode.col;
                    colNode.removeColumn();
                    restores.add(colNode::restoreColumn);
                    colNode = colNode.down;
                    while (!colNode .equals(colNode.col)) {
                        if (!colNode.right.equals(colNode)) {
                            colNode.right.removeRow();
                            restores.add(colNode.right::restoreRow);
                        }
                        colNode = colNode.down;
                    }
                    rowNode = rowNode.right;
                    if (rowNode.equals(selectedRow.right)) {
                        break;
                    }
                }
            }
            if (danceLinkX(head, answers)) {
//            #while len(restores): restores.pop()()
                return true;
            }
            answers.remove(answers.size() - 1);
            while (restores.size() > curRestoresCount) {
                Runnable method = restores.get(restores.size() - 1);
                method.run();
                restores.remove(method);
            }
            selectedRow = selectedRow.down;
        }
        while (restores.size() > 0) {
            Runnable method = restores.get(restores.size() - 1);
            method.run();;
            restores.remove(method);
        }
        return false;
    }


    static Map<String, Integer> gen() {
        Map<String, Integer> initData = initLocationDict(11);
        CrossCycleLinkNode<String> head = getSudokuLinkedList(initData);
        List<String> ans = new ArrayList<String>();
        danceLinkX(head, ans);
        if (ans.size() > 0) {
            Map<String, Integer> map = getSudokuMap(ans);
            head = getSudokuLinkedList(initData);
            ans.clear();
            danceLinkX(head, ans);
            if (ans.size() > 0) {
                return map;
            }
            return gen();
        }
        return gen();

    }

    static boolean check(Map<String, Integer> data) {
        CrossCycleLinkNode<String> head = getSudokuLinkedList(data);
        List<String> ans = new ArrayList<String>();
        danceLinkX(head, ans);
        return ans.size() > 0;
    }


}
