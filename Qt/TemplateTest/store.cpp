#include "store.h"

//using namespace std;

template<class T>
Store<T>::Store():haveValue(false){}

template<class T>
T &Store<T>::getElem() {
    if(!haveValue){
        std::cout <<"NO item present!"<<std::endl;
        exit(1);
    }

    return item;
}


template<class T>
void Store<T>::putElem(const T &x){
    haveValue = true;
    item = x;
}
