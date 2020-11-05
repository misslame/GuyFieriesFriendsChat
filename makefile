CC = g++

all: main.o mongoose.o a.out

main.o: source/main.cpp header/mongoose.h
	$(CC) -c source/main.cpp

mongoose.o: source/mongoose.c header/mongoose.h
	$(CC) -c source/mongoose.c
	
a.out: main.o mongoose.o 
	$(CC) main.o mongoose.o -o a.out    