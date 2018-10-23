#include <QCoreApplication>

int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);
    print();
    return a.exec();
}


void print(int i = 0){

    cout << "hello world" << endl;
}
