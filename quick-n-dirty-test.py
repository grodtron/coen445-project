import socket

udpsock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
tcpsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

tcpsock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serversock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

udpsock.bind(('localhost', 55655))
tcpsock.bind(('localhost', 55655))
serversock.bind(('localhost', 55655))

serversock.listen(1)


address = ('localhost', 12358)
register = b'\x04\x00\x06Gordon\x7f\x00\x00\x01\xd9\x67'
offer    = b'\x06\x00\x06Gordon\x7f\x00\x00\x01\tSome Shit\x00\n'

udpsock.sendto(register, address)
udpsock.recv(1024)

conn, client = serversock.accept()

udpsock.sendto(offer, address)
offerconf = udpsock.recv(1024)

port = ((0xff & ord(offerconf[2])) << 8) | (0xff & ord(offerconf[3]))

tcpsock.connect(('localhost', port))

tcpsock.send(b"\x00\0\0\0\0\0\0")
#resp = tcpsock.recv(100)

#print (repr(resp))

print ("receiving from server")
print (repr(conn.recv(100)))

while True:
	try:
		eval(raw_input(">>> "))
	except Exception as e:
		print(e)