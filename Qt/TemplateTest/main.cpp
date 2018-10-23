#include <QCoreApplication>
#include <iostream>
//#include "store.h"

using namespace std;

//template<typename T>
//T abs(T x) {
//    return x < 0?-x:x;
//}

struct Student {
    int id;
    float gpa;
};

template<typename T>
class Store
{
public:
    Store();
    T &getElem();
    void putElem(const T &x);
private:
    T item;
    bool haveValue;
};


template<typename T>
Store<T>::Store():haveValue(false){}

template<typename T>
T &Store<T>::getElem() {
    if(!haveValue){
        std::cout <<"NO item present!"<<std::endl;
        exit(1);
    }

    return item;
}


template<typename T>
void Store<T>::putElem(const T &x){
    haveValue = true;
    item = x;
}




int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);

//    int n = -5;
//    double d = -5.5;
//    cout << abs(n) <<endl;
//    cout << abs(d) <<endl;

    Store<int>s1, s2;
    s1.putElem(3);
    s2.putElem(-7);
    cout<<s1.getElem()<<" "<<s2.getElem()<<endl;

    Student g = {1000, 23};
    Store<Student> s3;
    s3.putElem(g);
    cout<<"The student id is " << s3.getElem().id<<endl;

    return a.exec();
}
