package com.charles;

import java.util.ArrayList;
import java.util.Stack;

class InfixEvaluator {
    public static void main(String[] args) {
        try {
            System.out.println(arithmetic(args[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Stack<Double> operandStack = new Stack<>();

    private static Stack<String> operatorStack = new Stack<>();


    private static final String OPERATORS = "+-/*%^()[]{}";
    private static final String NONBRACES = "+-/*%^";
    private static final int[] PRECEDENCE = {1, 1, 2, 2, 3, 3, -1, -1, -1, -1, -1, -1};

    static ArrayList<String> input = new ArrayList<>();


    private static ArrayList<String> inputCleaner(String postfix) {
        StringBuilder sb = new StringBuilder();
        String noSpaces = postfix.replace(" ", "");
        try {
            for (int i = 0; i < noSpaces.length(); i++) {
                char c = noSpaces.charAt(i);
                boolean isNum = (c >= '0' && c <= '9');

                if (isNum) {
                    sb.append(c);
                    if (i == noSpaces.length() - 1) {
                        input.add(sb.toString());
                        sb.delete(0, sb.length());
                    }
                } else if (c == '.') {
                    for (int j = 0; j < sb.length(); j++) {
                        if (sb.charAt(j) == '.') {
                            throw new IllegalArgumentException("You can't have two decimals in a number.");
                        } else if (j == sb.length() - 1) {
                            sb.append(c);
                            j = (sb.length() + 1);
                        }
                    }
                    if (sb.length() == 0) {
                        sb.append(c);
                    }
                    if (i == noSpaces.length() - 1) {
                        throw new IllegalArgumentException("You can't end your equation with a decimal!");
                    }
                } else if (OPERATORS.indexOf(c) != -1) {
                    if (sb.length() != 0) {
                        input.add(sb.toString());
                        sb.delete(0, sb.length());
                    }
                    sb.append(c);
                    input.add(sb.toString());
                    sb.delete(0, sb.length());
                } else {
                    throw new IllegalArgumentException("Make sure your input only contains numbers, operators, or parantheses/brackets/braces.");
                }
            }

            int numLP = 0;
            int numRP = 0;
            int numLB = 0;
            int numRB = 0;
            int numLBr = 0;
            int numRBr = 0;

            for (String s : input) {
                switch (s) {
                    case "(":
                        numLP++;
                        break;
                    case "[":
                        numLB++;
                        break;
                    case "{":
                        numLBr++;
                        break;
                    case ")":
                        numRP++;
                        break;
                    case "]":
                        numRB++;
                        break;
                    case "}":
                        numRBr++;
                        break;
                    default: //do nothing
                        break;
                }

            }
            if (numLP != numRP || numLB != numRB || numLBr != numRBr) {
                throw new IllegalArgumentException("The number of brackets, braces, or parentheses don't match up!");
            }

            int doop = 0;
            int scoop = 0;
            int foop = 0;
            for (String awesome : input) {
                switch (awesome) {
                    case "(":
                        doop++;
                        break;
                    case "[":
                        scoop++;
                        break;
                    case "{":
                        foop++;
                        break;
                    case ")":
                        doop--;
                        break;
                    case "]":
                        scoop--;
                        break;
                    case "}":
                        foop--;
                        break;
                    default: //do nothing
                        break;
                }
                if (doop < 0 || scoop < 0 || foop < 0) {
                    throw new IllegalArgumentException("The order of your parentheses, brackets, or braces is off.\nMake sure you open a set of parenthesis/brackets/braces before you close them.");
                }
            }
            if (NONBRACES.contains(input.get(input.size() - 1))) {
                throw new IllegalArgumentException("The input can't end in an operator");
            }
            return input;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getLocalizedMessage());
            return input;
        }
    }

    private static void processOperator(String op) throws IllegalArgumentException {
        if (operatorStack.empty() || op.equals("(") || op.equals("[") || op.equals("{")) {
            operatorStack.push(op);
        } else {
            //peek the operator stack and
            //let topOp be the top operator.
            String topOp = operatorStack.peek();
            if (precedence(op) > precedence(topOp)) {
                topOp = op;
                operatorStack.push(op);
            } else {
                //Pop all stacked operators with equal
                // or higher precedence than op.
                boolean handle = false;

                while (operandStack.size() >= 2 && !operatorStack.isEmpty()) {
                    double r = operandStack.pop();
                    double l = operandStack.pop();
                    String work = getNextNonBracerOperator();

                    doOperandWork(work, l, r);

                    if (!operatorStack.empty()) {
                        //reset topOp
                        topOp = operatorStack.peek();
                    }
                    handle = true;
                }
                if (operatorStack.size() < 2) {
                    handle = true;
                }
                if (!handle) {
                    throw new IllegalArgumentException("argument error");
                }

                //assert: Operator stack is empty or
                // current operator precedence > top of stack operator precedence.
                operatorStack.push(op);
            }
        }
    }

    private static String arithmetic(String g) throws IllegalArgumentException {
        ArrayList<String> expressions = inputCleaner(g);
        for (String expression : expressions) {
            if (!OPERATORS.contains(expression)) {
                operandStack.push(Double.parseDouble(expression));
            } else {
                processOperator(expression);
            }
        }
        boolean handle = false;
        while (operandStack.size() >= 2 && !operatorStack.isEmpty()) {

            double r = operandStack.pop();
            double l = operandStack.pop();
            String work = getNextNonBracerOperator();

            doOperandWork(work, l, r);
            handle = true;
        }
        if (operatorStack.size() < 2) {
            handle = true;
        }
        if (!handle) {
            throw new IllegalArgumentException("argument error");
        }
        if (operandStack.isEmpty())
            return null;
        return String.valueOf(operandStack.pop());
    }


    private static String getNextNonBracerOperator() {
        String work = "\0"; // \0 is null,
        while (!operatorStack.isEmpty() && NONBRACES.indexOf(work) == -1)
            work = operatorStack.pop();
        return work;
    }

    private static void doOperandWork(String work, double l, double r) throws IllegalArgumentException {
        switch (work) {
            case "+":
                operandStack.push(l + r);
                break;
            case "-":
                operandStack.push(l - r);
                break;
            case "*":
                operandStack.push(l * r);
                break;
            case "/":
                operandStack.push(l / r);
                break;
            case "%":
                operandStack.push(l % r);
                break;
            case "^":
                operandStack.push(Math.pow(l, r));
                break;
            default:
                throw new IllegalArgumentException("Invalid operand " + work);
        }
    }

    private static int precedence(String op) {
        return PRECEDENCE[OPERATORS.indexOf(op)];
    }
}