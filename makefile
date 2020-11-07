CC = g++

all: main.o mongoose.o a.out

main.o: server/source/main.cpp server/header/mongoose.h
	$(CC) -c server/source/main.cpp

mongoose.o: server/source/mongoose.c server/header/mongoose.h
	$(CC) -c server/source/mongoose.c
	
a.out: main.o mongoose.o 
	$(CC) main.o mongoose.o -o a.out    