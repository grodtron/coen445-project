import socket

from sys import argv
from time import sleep
from multiprocessing import Process, Lock

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
loop = True
mutex = Lock()

def register_client():
    register = b'\x04\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:][-4:-2],16)) + chr(int(hex(localport)[2:][-2:],16))
    with mutex:
        udpsock.sendto(register, address)
        udpsock.settimeout(server_timeout)
        try:
            response = udpsock.recv(1024)
        except Exception as e:
            print e
            return 0,0
    if ord(response[0]) != 3:
        print 0,0
    with mutex:
        serversock.settimeout(10)
        return serversock.accept()

def deregister_client():
    register = b'\x02\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:4],16)) + chr(int(hex(localport)[4:6],16))
    udpsock.sendto(register, address)

    with mutex:
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

def offer_item(item,price):
    offer    = b'\x06\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01'+ chr(len(item)) +item+chr(int(hex(price),16))+'\n'
    udpsock.sendto(offer, address)
    offerconf = udpsock.recv(1024)
    port = 256*ord(offerconf[2]) + ord(offerconf[3])
    return port

def bid_item(item):
    print "Place holder"

try:
    # client is the server's response variable 'address'
    conn, client = register_client()
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
        sleep(2)
        conn, client = register_client()
    except Exception as e:
        print "Could not connect to server. Please try another port number."
        print e
        loop = False
        exit()

def listentoUDP():
    with mutex:
        try:
            udpsock.settimeout(server_timeout)
            udpsock.recv(1024)
        except Exception as e:
            print "Nothing from udpsock"
def listentoSVR():
    with mutex:
        try:
            serversock.settimeout(server_timeout)
            serversock.recv(1024)
        except Exception as e:
            print "Nothing from serversock"

#p = Process(target = listentoUDP)
#p.start()
