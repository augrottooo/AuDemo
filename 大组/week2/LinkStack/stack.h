#ifndef STACK_H
#define STACK_H

// 链栈节点结构
typedef struct StackNode
{
    double data;            // 栈中存储的数据
    struct StackNode *next; // 指向下一个节点的指针
} StackNode, *LinkStackPtr;

// 链栈结构
typedef struct LinkStack
{
    LinkStackPtr top; // 栈顶指针
} LinkStack;

// 初始化栈
void initStack(LinkStack *s);

// 判断栈是否为空
int isEmpty(LinkStack *s);

// 入栈操作
void push(LinkStack *s, double data);

// 出栈操作
double pop(LinkStack *s);

// 获取栈顶元素
double getTop(LinkStack *s);

// 销毁栈
void destroyStack(LinkStack *s);

#endif