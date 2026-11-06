// Expression evaluator - C version of ExpressionEvaluator.java
//
// Uses stacks (a plain array plus a 'top' index, -1 = empty) to:
// 1. convert infix   3 + 4 * 2   to postfix   3 4 2 * +
// 2. convert infix   3 + 4 * 2   to prefix    + 3 * 4 2
// 3. evaluate the postfix expression
//
// Supports numbers (12, 3.5), + - * / ^ and parentheses.
// ^ is right-associative: 2 ^ 3 ^ 2 = 2 ^ (3 ^ 2) = 512.
//
// Build: gcc -std=c11 -Wall -Wextra expression_evaluator.c -o expr -lm
// Run:   ./expr "3 + 4 * 2 / (1 - 5) ^ 2"
#include <ctype.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX 256

static char err[96];   // message for the last error; empty means no error

static int fail(const char *msg, const char *detail) {
    snprintf(err, sizeof err, "%s%s", msg, detail);
    return 0;
}

static int is_operator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
}

static int precedence(char op) {
    if (op == '^') return 3;
    if (op == '*' || op == '/') return 2;
    if (op == '+' || op == '-') return 1;
    return 0;   // '(' has the lowest precedence
}

// Should the operator on the stack leave before the incoming one is pushed?
static int must_pop_first(char on_stack, char incoming) {
    if (precedence(on_stack) > precedence(incoming)) return 1;
    if (precedence(on_stack) == precedence(incoming) && incoming != '^') return 1;
    return 0;
}

// Appends "text " to out.
static void emit(char *out, const char *text) {
    strcat(out, text);
    strcat(out, " ");
}

static int infix_to_postfix(const char *infix, char *postfix) {
    char stack[MAX];
    int top = -1;
    size_t i = 0, n = strlen(infix);
    postfix[0] = '\0';

    while (i < n) {
        char c = infix[i];
        if (c == ' ') {
            i++;
        } else if (isdigit((unsigned char)c) || c == '.') {
            char number[64];
            int len = 0;
            while (i < n && (isdigit((unsigned char)infix[i]) || infix[i] == '.') && len < 63)
                number[len++] = infix[i++];
            number[len] = '\0';
            emit(postfix, number);
        } else if (c == '(') {
            stack[++top] = c;
            i++;
        } else if (c == ')') {
            while (top >= 0 && stack[top] != '(') {
                char op[2] = {stack[top--], '\0'};
                emit(postfix, op);
            }
            if (top < 0) return fail("unmatched )", "");
            top--;   // discard the '('
            i++;
        } else if (is_operator(c)) {
            while (top >= 0 && stack[top] != '(' && must_pop_first(stack[top], c)) {
                char op[2] = {stack[top--], '\0'};
                emit(postfix, op);
            }
            stack[++top] = c;
            i++;
        } else {
            char d[2] = {c, '\0'};
            return fail("invalid character: ", d);
        }
    }
    while (top >= 0) {
        if (stack[top] == '(') return fail("unmatched (", "");
        char op[2] = {stack[top--], '\0'};
        emit(postfix, op);
    }
    size_t len = strlen(postfix);
    if (len > 0) postfix[len - 1] = '\0';   // drop the trailing space
    return 1;
}

static int is_operator_token(const char *t) { return t[0] != '\0' && t[1] == '\0' && is_operator(t[0]); }

// Splits 'postfix' on spaces into tokens[]; returns the count.
static int tokenize(char *postfix, char *tokens[]) {
    int count = 0;
    for (char *t = strtok(postfix, " "); t != NULL; t = strtok(NULL, " ")) tokens[count++] = t;
    return count;
}

static int infix_to_prefix(const char *infix, char *prefix) {
    char postfix[MAX * 2];
    char *tokens[MAX];
    char stack[MAX][MAX * 2];
    int top = -1;

    if (!infix_to_postfix(infix, postfix)) return 0;
    int count = tokenize(postfix, tokens);

    for (int i = 0; i < count; i++) {
        if (is_operator_token(tokens[i])) {
            if (top < 1) return fail("not enough numbers for ", tokens[i]);
            char combined[MAX * 6];
            snprintf(combined, sizeof combined, "%s %s %s", tokens[i], stack[top - 1], stack[top]);
            top -= 2;
            strcpy(stack[++top], combined);
        } else {
            strcpy(stack[++top], tokens[i]);
        }
    }
    if (top != 0) return fail("invalid expression", "");
    strcpy(prefix, stack[0]);
    return 1;
}

static int evaluate_postfix(const char *postfix_in, double *result) {
    char postfix[MAX * 2];
    char *tokens[MAX];
    double stack[MAX];
    int top = -1;

    strcpy(postfix, postfix_in);
    int count = tokenize(postfix, tokens);

    for (int i = 0; i < count; i++) {
        if (is_operator_token(tokens[i])) {
            if (top < 1) return fail("not enough numbers for ", tokens[i]);
            double right = stack[top--];
            double left = stack[top--];
            double r;
            switch (tokens[i][0]) {
                case '+': r = left + right; break;
                case '-': r = left - right; break;
                case '*': r = left * right; break;
                case '/':
                    if (right == 0) return fail("division by zero", "");
                    r = left / right;
                    break;
                default:  r = pow(left, right); break;
            }
            stack[++top] = r;
        } else {
            stack[++top] = atof(tokens[i]);
        }
    }
    if (top != 0) return fail("invalid expression", "");
    *result = stack[0];
    return 1;
}

int main(int argc, char *argv[]) {
    const char *infix = (argc > 1) ? argv[1] : "3 + 4 * 2 / (1 - 5) ^ 2";
    char postfix[MAX * 2], prefix[MAX * 2];
    double result;

    if (infix_to_postfix(infix, postfix)) {
        printf("Infix:   %s\n", infix);
        printf("Postfix: %s\n", postfix);
        if (infix_to_prefix(infix, prefix)) {
            printf("Prefix:  %s\n", prefix);
            if (evaluate_postfix(postfix, &result)) {
                // match Java: whole numbers print as 30.0, others as 3.5
                if (result == floor(result) && fabs(result) < 1e15) printf("Result:  %.1f\n", result);
                else printf("Result:  %g\n", result);
                return 0;
            }
        }
    }
    printf("Error: %s\n", err);
    return 0;
}
