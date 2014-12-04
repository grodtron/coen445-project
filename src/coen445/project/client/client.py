import socket
import os
import threading

from sys import argv
from time import sleep

try:
    localport = int(argv[1])
    username = argv[2]
except:
    try:
        localport = int(raw_input("Please enter a valid port number between 1025 and 65535: "))
        username = raw_input("Please enter a valid username: ")
    except:
        print "Invalid localport/username combination. Exiting..."
        exit()

if localport > 0xFFFF or localport < 1024:
    print "Bad port number. Please try again. Exiting..."
    exit()

server_timeout = 2
IP = 'localhost'

udpsock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
#tcpsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

#tcpsock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serversock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

udpsock.bind((IP, localport))
#tcpsock.bind((IP, localport))
serversock.bind((IP, localport))

serversock.listen(1)

address = (IP, 12358)

auction = []
# example of adding an item 
    # auction.append([$description,$port,$current_bid,$client_bid,$itemsock])
loop = True
mutex = threading.Lock()
auction_mutex = threading.Lock()

def register():
    register = b'\x04\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:][-4:-2],16)) + chr(int(hex(localport)[2:][-2:],16))
    mutex.acquire()
    udpsock.settimeout(server_timeout)
    udpsock.sendto(register, address)
    try:
        response = udpsock.recv(1024)
        mutex.release()
    except Exception as e:
        print e
        mutex.release()
        return 0,0
    if ord(response[0]) != 3:
        return 0,0
    mutex.acquire()
    serversock.settimeout(10)
    s = serversock.accept()
    serversock.close()
    mutex.release()
    return s

def deregister():
    register = b'\x02\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:4],16)) + chr(int(hex(localport)[4:6],16))
    udpsock.sendto(register, address)

    mutex.acquire()
    try:
        udpsock.settimeout(server_timeout)
        response = udpsock.recv(1024)
        print repr(response)
        print "Deregistering success! Exiting..."
        loop = False
        exit()
    except Exception as e:
        print "Error while deregistering."
        print e
    mutex.release()

def offer(item,price):
    offer    = b'\x06\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01'+ chr(len(item)) +item+chr(int(hex(price)[2:][-4:-2].zfill(1),16))+chr(int(hex(price)[2:][-2:].zfill(1),16))
    mutex.acquire()
    try:
        udpsock.sendto(offer, address)
        offerconf = udpsock.recv(1024)
    except Exception as e: 
        print e
    mutex.release()
    #print 256*ord(offerconf[2]) + ord(offerconf[3])
    return True

def bid(item, price):
    for i in range(len(auction)):
        if auction[i][0] == item:
            offer    = b'\x00\0\0'+ chr(int(hex(auction[i][1])[-4:-2].zfill(1),16))+chr(int(hex(auction[i][1])[-2:].zfill(1),16)) + chr(int(hex(price)[2:][-4:-2].zfill(1),16))+chr(int(hex(price)[2:][-2:].zfill(1),16))
            print "Printing offer"
            print repr(offer)
            mutex.acquire()
            print "Acquired mutex!"
            #auction[i][4].connect((IP,auction[i][1]))
            auction[i][4].send(offer)
            try:
                auction[i][4].settimeout(server_timeout)
                #print "Printing receiving"
                resp = auction[i][4].recv(1024)
                #print repr(resp)
                #tcpsock.close()
                mutex.release()
            except Exception as e:
                print "Error in bidding"
                print e
                #tcpsock.close()
                mutex.release()
                return False

            if ord(resp[0]) == 1:
                auction_mutex.acquire()
                auction[i][2] = price
                auction[i][3] = price
                auction_mutex.release()
            return True

def remove_name(item):
    for i in range(len(auction)):
        if auction[i][0] == item:
            auction_mutex.acquire()
            auction[i][4].close()
            auction.pop(i)
            auction_mutex.release()
            return True

def remove_ID(item):
    for i in range(len(auction)):
        if auction[i][1] == item:
            auction_mutex.acquire()
            auction[i][4].close()
            auction.pop(i)
            auction_mutex.release()
            return True

def update_ID(item,price):
    for i in range(len(auction)):
        if auction[i][1] == item:
            auction_mutex.acquire()
            auction[i][2]=price
            auction_mutex.release()
            return True

try:
    # client is the server's response variable 'address'
    conn, client = register()
except Exception as e:
    print "Could not connect to server. Please try another port number."
    print e
    loop = False
    exit()

# If connection fails, sleep 2 seconds and try again. Eventually exit if 
# catastrophe happens
while (conn == 0) and (client == 0):
    try:
        # client is the server's response variable 'address'
        print "Could not connect to server. Attempting again..."
        conn, client = register_client()
    except Exception as e:
        print "Could not connect to server. Please try another port number."
        print e
        loop = False
        exit()

def listentoUDP():
    mutex.acquire()
    try:
        udpsock.settimeout(server_timeout)
        resp = udpsock.recv(1024)
        mutex.release()
    except Exception as e:
        mutex.release()
        return False

def listentoSVR():
    mutex.acquire()
    try:
        serversock.settimeout(server_timeout)
        resp = serversock.recv(1024)
        mutex.release()
    except Exception as e:
        mutex.release()
        return False


def listentoConn():
    mutex.acquire()
    try:
        conn.settimeout(server_timeout)
        resp = conn.recv(1024)
        mutex.release()
    except Exception as e:
        mutex.release()
        return False

    #for i in range(len(resp)):
        #print repr(resp[i])
    print "Received Conn message:"
    print repr(resp)
    try:
        if ord(resp[0]) == 8:
            port = 256*ord(resp[1]) + ord(resp[2])
            description = resp[4:4+ord(resp[3])]
            price = 256*ord(resp[-4])+ord(resp[-3])
            for i in range(len(auction)):
                if auction[i][1] == port:
                    return False
                if auction[i][0] == description:
                    print "There exists an item with the same name. Please enter another name."
                    return False
            #print port
            #print description
            #print price
            itemsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            itemsock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            itemsock.bind((IP,localport))
            itemsock.connect((IP,port))
            auction_mutex.acquire()
            print "New item!"
            auction.append([description,port,price,0,itemsock])
            auction_mutex.release()
        elif ord(resp[0]) == 2:
            #Someone else won the item
            name = resp[4:4+ord(resp[3])]
            port = 256*ord(resp[1]) + ord(resp[2])
            for i in range(len(auction)):
                if auction[i][1] == port:
                    item = auction[i][0]
            print name + " won the item: " + item + "!"
            remove_ID(port)
        elif ord(resp[0]) == 1:
            print "Someone bidded on an item!"
            port = 256*ord(resp[1]) + ord(resp[2])
            price = 256*ord(resp[3]) + ord(resp[4])
            update_ID(port,price)
    except Exception as e:
        print e
        

def listentoAuctions():
    for i in range(len(auction)):
        mutex.acquire()      
        try:
            auction[i][4].settimeout(server_timeout)
            #print "Printing receiving"
            resp = auction[i][4].recv(1024)
            #print repr(resp)
            mutex.release()
            #print "Updated item!"
            if resp=="":
                return False
            print "Got auction update:"
            print repr(resp)
        except Exception as e:
            #print "No news from an auction"
            #print e
            mutex.release()
            return False
        if ord(resp[0]) == 1:
            print "Someone bidded on an item!"
            port = 256*ord(resp[1]) + ord(resp[2])
            price = 256*ord(resp[3]) + ord(resp[4])
            update_ID(port,price)
        elif ord(resp[0]) == 2:
            port = 256*ord(resp[1]) + ord(resp[2])
            name = resp[4:4+ord(resp[3])]
            item = auction[i][0]
            print "Congratulations! You won the item: " + item + " from seller: " + name + "!"
            remove_ID(port)

def check_pid(pid):        
    # Check For the existence of a unix pid.
    try:
        os.kill(pid, 0)
    except OSError:
        return False
    else:
        return True

def listen(pid):
    while check_pid(pid):
        listentoUDP()
        #listentoSVR()
        listentoConn()
        listentoAuctions()


p = threading.Thread(target = listen, args = (os.getpid(),))
p.start()
