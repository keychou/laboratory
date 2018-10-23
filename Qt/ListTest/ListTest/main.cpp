#include <QCoreApplication>
#include <iostream>
#include <list>
using namespace std;

int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);

    //用LISTINT创建一个list对象
    list<int> listOne;
    //声明i为迭代器
    list<int>::iterator i;

    listOne.push_front(3);
    listOne.push_front(2);
    listOne.push_front(1);

    listOne.push_back(4);
    listOne.push_back(5);
    listOne.push_back(6);

    cout << "listOne.begin()--- listOne.end():" << endl;
    for (i = listOne.begin(); i != listOne.end(); ++i){
        cout << *i << " ";
    }
    cout << endl;
    cout << listOne.size() << endl;

    listOne.erase(listOne.begin());
    for (i = listOne.begin(); i != listOne.end(); ++i){
        cout << *i << " ";
    }

    cout << endl<< listOne.size() << endl;

    listOne.erase(listOne.begin());
    for (i = listOne.begin(); i != listOne.end(); ++i){
        cout << *i << " ";
    }
    cout << endl << listOne.size() << endl;



//    list<int>::reverse_iterator ir;
//    cout << "listOne.rbegin()---listOne.rend():" << endl;
//    for (ir = listOne.rbegin(); ir != listOne.rend(); ir++) {
//        cout << *ir << " ";
//    }
//    cout << endl;

//    int result = accumulate(listOne.begin(), listOne.end(), 0);
//    cout << "Sum=" << result << endl;
//    cout << "------------------" << endl;

    return a.exec();
}
