#include <stdio.h>
#include <stdlib.h>

//int main()
//{
//    int array[] = {11,3,5,7,8,1};
//
//   display (swapArray(array), 6);

//    return 0;
//}

    void display(int a[], size_t length){
        for (int i = 0; i < 6; i++){
            printf("%d ", a[i]);
        }
    }
    int swapArray(int array[]){
        for (int i = 0; i < 5; i++){
            int temp = array[i];
            array[i] = array[i + 1];
            array[i + 1] = temp;
        }
        return array;
    }
