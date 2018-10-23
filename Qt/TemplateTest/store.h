#ifndef STORE_H
#define STORE_H

#include <iostream>
#include <cstdlib>

struct Student {
    int id;
    float gpa;
};

template<class T>
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

#endif // STORE_H
