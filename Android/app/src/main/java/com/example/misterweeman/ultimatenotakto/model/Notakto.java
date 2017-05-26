package com.example.misterweeman.ultimatenotakto.model;

public class Notakto {

    //funzioni per controllare la presenza di x nelle varie direzioni

    private static boolean chkUpLeftUpLeft(Board brd, int x, int y) {
        boolean lost = false;
        int x1 = x - 1;
        int y1 = y - 1;
        int x2 = x - 2;
        int y2 = y - 2;
        if (x1 >= 0 && x2 >= 0 && y1 >= 0 && y2 >= 0) {
            if (brd.at(x1, y1) && brd.at(x2, y2)) {
                System.out.println("chkULUL");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkLeftLeft(Board brd, int x, int y) {
        boolean lost = false;
        int x1 = x - 1;
        int x2 = x - 2;
        if (x1 >= 0 && x2 >= 0) {
            if (brd.at(x1, y) && brd.at(x2, y)) {
                System.out.println("chkLL");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkDownLeftDownLeft(Board brd, int x, int y) {
        boolean lost = false;
        int x1 = x - 1;
        int y1 = y + 1;
        int x2 = x - 2;
        int y2 = y + 2;
        if (x1 >= 0 && x2 >= 0 && y1 < brd.getSize() && y2 < brd.getSize()) {
            if (brd.at(x1, y1) && brd.at(x2, y2)) {
                System.out.println("chkDLDL");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkDownDown(Board brd, int x, int y) {
        boolean lost = false;
        int y1 = y + 1;
        int y2 = y + 2;
        if (y1 < brd.getSize() && y2 < brd.getSize()) {
            if (brd.at(x, y1) && brd.at(x, y2)) {
                System.out.println("chkDD");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkDownRightDownRight(Board brd, int x, int y) {
        boolean lost = false;
        int x1 = x + 1;
        int y1 = y + 1;
        int x2 = x + 2;
        int y2 = y + 2;
        if (x1 < brd.getSize() && x2 < brd.getSize() && y1 < brd.getSize() && y2 < brd.getSize()) {
            if (brd.at(x1, y1) && brd.at(x2, y2)) {
                System.out.println("chkDRDR");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkRightRight(Board brd, int x, int y) {
        boolean lost = false;
        int x1 = x + 1;
        int x2 = x + 2;
        if (x1 < brd.getSize() && x2 < brd.getSize()) {
            if (brd.at(x1, y) && brd.at(x2, y)) {
                System.out.println("chkDD");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkUpRightUpRight(Board brd, int x, int y) {
        boolean lost = false;
        int x1 = x + 1;
        int y1 = y - 1;
        int x2 = x + 2;
        int y2 = y - 2;
        if (x1 < brd.getSize() && x2 < brd.getSize() && y1 >= 0 && y2 >= 0) {
            if (brd.at(x1, y1) && brd.at(x2, y2)) {
                System.out.println("chkDLDL");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkUpUp(Board brd, int x, int y) {
        boolean lost = false;
        int y1 = y - 1;
        int y2 = y - 2;
        if (y1 >= 0 && y2 >= 0) {
            if (brd.at(x, y1) && brd.at(x, y2)) {
                System.out.println("chkUU");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkUpLeftDownRight(Board brd, int x, int y) {
        boolean lost = false;
        int x1 = x - 1;
        int y1 = y - 1;
        int x2 = x + 1;
        int y2 = y + 1;
        if (x1 >= 0 && x2 < brd.getSize() && y1 >= 0 && y2 < brd.getSize()) {
            if (brd.at(x1, y1) && brd.at(x2, y2)) {
                System.out.println("chkULDR");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkLeftRight(Board brd, int x, int y) {
        boolean lost = false;
        int x1 = x - 1;
        int x2 = x + 1;
        if (x1 >= 0 && x2 < brd.getSize()) {
            if (brd.at(x1, y) && brd.at(x2, y)) {
                System.out.println("chkUD");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkUpRightDownLeft(Board brd, int x, int y) {
        boolean lost = false;
        int x1 = x - 1;
        int y1 = y + 1;
        int x2 = x + 1;
        int y2 = y - 1;
        if (x1 >= 0 && x2 < brd.getSize() && y1 < brd.getSize() && y2 >= 0) {
            if (brd.at(x1, y1) && brd.at(x2, y2)) {
                System.out.println("chkURDL");
                lost = true;
            }
        }
        return lost;
    }

    private static boolean chkUpDown(Board brd, int x, int y) {
        boolean lost = false;
        int y1 = y - 1;
        int y2 = y + 1;
        if (y1 >= 0 && y2 < brd.getSize()) {
            if (brd.at(x, y1) && brd.at(x, y2)) {
                System.out.println("chkUD");
                lost = true;
            }
        }
        return lost;
    }

    public static boolean checkBoardForLost(Board board, int x, int y) {
        return chkUpLeftUpLeft(board, x, y) || chkUpUp(board, x, y) || chkUpRightUpRight(board, x, y)
                || chkLeftLeft(board, x, y) || chkLeftRight(board, x, y) || chkRightRight(board, x, y)
                || chkDownLeftDownLeft(board, x, y) || chkDownDown(board, x, y) || chkDownRightDownRight(board, x, y)
                || chkUpLeftDownRight(board, x, y) || chkUpDown(board, x, y) || chkUpRightDownLeft(board, x, y);
    }
}
