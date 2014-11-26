import socket

from sys import argv
from time import sleep
from multiprocessing import Thread, Lock

try:
    localport = int(argv[1])
    username = argv[2]
except:
    try:
        localport = int(raw_input("Please enter a valid port number: "))
        username = raw_input("Please enter a valid username: ")
    except:
        print "Unspecified error. Exiting..."
        exit()

if localport > 0xFFFF:
    print "Bad port. Please try again. Exiting..."
    exit()

udpsock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
tcpsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

tcpsock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serversock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

udpsock.bind(('localhost', localport))
tcpsock.bind(('localhost', localport))
serversock.bind(('localhost', localport))

serversock.listen(1)

address = ('localhost', 12358)

auction = []
# example of adding an item 
    # car = {"port" : 14533, "current_bid": 10, "client_bid": 10}
    # auction.append(car)

def register_client():
    register = b'\x04\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:][-4:-2],16)) + chr(int(hex(localport)[2:][-2:],16))
    udpsock.sendto(register, address)
    udpsock.settimeout(10)
    try:
        response = udpsock.recv(1024)
    except Exception as omg_gordon_fine:
        print omg_gordon_fine
        return 0,0
    if ord(response[0]) != 3:
        print 0,0
    serversock.settimeout(10)
    return serversock.accept()

def deregister_client():
    register = b'\x02\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:4],16)) + chr(int(hex(localport)[4:6],16))
    udpsock.sendto(register, address)
    udpsock.recv(1024)
    serversock.settimeout(10)

    try:
        result = serversock.accept()
        print result
    except:
        print "Server took too long to respond. Force quiting..."
    # TO-DO: CONFIRM THAT SERVER ACKNOWLEDGES DEREGISTER REQUEST
    exit()

def offer_item(item,price):
    offer    = b'\x06\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01'+ chr(len(item)) +item+chr(int(hex(price),16))+'\n'
    udpsock.sendto(offer, address)
    offerconf = udpsock.recv(1024)
    port = 256*ord(offerconf[2]) + ord(offerconf[3])
    return port

def bid_item(item):
    # 
    print "Place holder"

try:
    # client is the server's response variable 'address'
    conn, client = register_client()
except Exception as e:
    print "Could not connect to server. Please try another port number."
    print e
    exit()

while (conn == 0) and (client == 0):
    try:
        # client is the server's response variable 'address'
        print "Could not connect to server. Attempting again..."
        sleep(2)
        conn, client = register_client()
    except Exception as e:
        print "Could not connect to server. Please try another port number."
        print e
        exit()

#print ("receiving from server")
#print (repr(conn.recv(100)))
