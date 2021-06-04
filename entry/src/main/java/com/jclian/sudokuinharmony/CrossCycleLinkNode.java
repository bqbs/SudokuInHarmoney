package com.jclian.sudokuinharmony;

public class CrossCycleLinkNode<T> {
    CrossCycleLinkNode<T> up = this;
    CrossCycleLinkNode<T> down = this;
    CrossCycleLinkNode<T> left = this;
    CrossCycleLinkNode<T> right = this;
    CrossCycleLinkNode<T> col = this;
    T mValue;

    String mRow;

    public CrossCycleLinkNode(T value, String row) {
        this.col = this;
        this.up = this;
        this.down = this;
        this.left = this;
        this.right = this;
        this.mValue = value;
        this.mRow = row;
    }


    public void removeColumn() {
        CrossCycleLinkNode<T> node = this;
        while (true) {
            node.left.right = node.right;
            node.right.left = node.left;
            node = node.down;
            if (node == this) break;
        }
    }

    public void restoreColumn() {
        CrossCycleLinkNode<T> node = this;
        while (true) {
            node.left.right = node;
            node.right.left = node;
            node = node.down;
            if (node == this) {
                break;
            }
        }
    }

    public void removeRow() {
        CrossCycleLinkNode<T> node = this;
        while (true) {
            node.up.down = node.down;
            node.down.up = node.up;
            node = node.right;
            if (node == this) {
                break;
            }
        }
    }


    public void restoreRow() {
        CrossCycleLinkNode<T> node = this;
        while (true) {
            node.up.down = node;
            node.down.up = node;
            node = node.right;
            if (node == this) {
                break;
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

