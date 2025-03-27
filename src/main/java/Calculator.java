import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator implements ActionListener{
    // API Key for OpenAI API
    // API KEY REMOVED TO PUSH CODE TO GITHUB
    // ADD KEY IN ORDER TO USE CALCULATOR
    String api_key = "";

    // String used to gather input from calculator
    String previousExpression = " ";
    String currentExpression = "";
    String response = "";

    // answer displayed
    boolean answerDisplayed = false;

    // Buttons for numbers
    JButton one;
    JButton two;
    JButton three;
    JButton four;
    JButton five;
    JButton six;
    JButton seven;
    JButton eight;
    JButton nine;
    JButton zero;

    // Buttons for basic operations
    JButton add;
    JButton subtract;
    JButton multiply;
    JButton divide;

    // Buttons for trig
    JButton sin;
    JButton cos;
    JButton tan;
    JButton arcsin;
    JButton arccos;
    JButton arctan;

    // Buttons for log
    JButton log;
    JButton ln;

    // Buttons for power
    JButton root;
    JButton exponent;


    // Miscellaneous buttons
    JButton decimal;
    JButton negative;
    JButton equals;
    JButton leftParenthesis;
    JButton rightParenthesis;
    JButton pi;
    JButton euler;
    JButton clear;

    // display text
    JLabel previous;
    JLabel display;

    // font
    Font font = new Font("Arial", Font.PLAIN, 34);
    Calculator(){
        // JFrame initialization
        JFrame jfrm = new JFrame("AI Calculator");
        jfrm.getContentPane().setLayout(new FlowLayout());
        jfrm.setSize(600,550);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Display panel
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setPreferredSize(new Dimension(600,100));
        displayPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        displayPanel.setBackground(Color.BLACK);

        previous = new JLabel(previousExpression);
        previous.setForeground(Color.darkGray);
        previous.setFont(font);
        previous.setAlignmentX(Component.RIGHT_ALIGNMENT);

        display = new JLabel(currentExpression);
        display.setForeground(Color.white);
        display.setFont(font);
        display.setAlignmentX(Component.RIGHT_ALIGNMENT);

        displayPanel.add(previous);
        displayPanel.add(display);

        jfrm.add(displayPanel);

        // Buttons panel
        JPanel buttons = new JPanel(new GridLayout(8,4));
        buttons.setPreferredSize(new Dimension(600,400));

        // numbers
        one = new JButton("1");
        two = new JButton("2");
        three = new JButton("3");
        four = new JButton("4");
        five = new JButton("5");
        six = new JButton("6");
        seven = new JButton("7");
        eight = new JButton("8");
        nine = new JButton("9");
        zero = new JButton("0");

        // operations
        add = new JButton("+");
        subtract = new JButton("-");
        multiply = new JButton("×");
        divide = new JButton("÷");

        // trig
        sin = new JButton("sin");
        cos = new JButton("cos");
        tan = new JButton("tan");
        arcsin = new JButton("arcsin");
        arccos = new JButton("arccos");
        arctan = new JButton("arctan");

        // log
        log = new JButton("log");
        ln = new JButton("ln");

        // power
        root = new JButton("√");
        exponent = new JButton("^");

        // misc
        decimal = new JButton(".");
        negative = new JButton("(-)");
        equals = new JButton("=");
        leftParenthesis = new JButton("(");
        rightParenthesis = new JButton(")");
        pi = new JButton("π");
        euler = new JButton("e");
        clear = new JButton("clear");

        JButton[] list = {log, ln, root, clear,
                arcsin, arccos, arctan, euler,
                sin, cos, tan, pi,
                leftParenthesis, rightParenthesis, exponent, divide,
                seven, eight, nine, multiply,
                four, five, six, subtract,
                one, two, three, add,
                zero, decimal, negative, equals};

        for (int i = 0; i < list.length; i++){
            list[i].setFont(font);
            list[i].addActionListener(this);
            buttons.add(list[i], i);
        }

        jfrm.add(buttons);

        jfrm.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae){
        String[] buttonInputs = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
                "+", "-", "×", "÷",
                "sin", "cos", "tan", "arcsin", "arccos", "arctan",
                "log", "ln",
                "√", "^", ".", "(", ")", "π", "e", "clear", "(-)", "="};

        if (answerDisplayed){
            previousExpression = currentExpression; // URGENT
            currentExpression = ""; // switch with next line when AI implemented
            answerDisplayed = false;
        }

        for (int x = 0; x < buttonInputs.length; x++){
            if (ae.getActionCommand().equals(buttonInputs[x])){
                if (buttonInputs[x] == "clear"){
                    currentExpression = "";
                }
                else if (buttonInputs[x] == "="){
                    // run method to compute
                    response = getResponse(currentExpression);
                    answerDisplayed = true;
                    previousExpression = currentExpression;
                    currentExpression = response;
                }
                else if (buttonInputs[x] == "(-)"){
                    currentExpression = currentExpression.concat("(-");
                }
                else {
                    currentExpression = currentExpression.concat(buttonInputs[x]);
                }
            }
        }

        previous.setText(previousExpression);
        display.setText(currentExpression);
    }

    public String getResponse(String expression){
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(api_key)
                .build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("Act as a calculator that can only respond with the value of the answer or \"Error\". Evaluate" +
                        "the following expression. If the expression contains trigonometry, evaluate in degrees. If the answer is an approximate" +
                        "respond with the value of the approximation. If expression contains 'π', treat 'π' as a variable with" +
                        "the value 3.14159265359. If expression contains 'e', treat 'e' as a variable with the value" +
                        "2.718281828459045. Never include letters from any alphabet in response: \"" + expression +"\"")
                //.addUserMessage("Calculate the expression \""+ expression + "\". reply with just the correct answer")
                .model(ChatModel.GPT_4O)
                .build();
        ChatCompletion chatCompletion = client.chat().completions().create(params);

        String answer = chatCompletion.choices().get(0).message().content().orElse("No answer recieved");
        System.out.println(expression);
        System.out.println(answer);
        return answer;
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Calculator();
            }
        });
    }


}
