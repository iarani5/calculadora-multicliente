# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""

import sys
import socket as sk

host = "localhost"
port = 6000

sCliente =  sk.socket()
sCliente.connect((host, port))
print("Conectado")

while True: 
    inp = input("Texto para enviar: ")
    print("Enviar:", inp)
    salida = inp.encode("UTF8")
    print("Salida antes de enviar:", salida.decode("utf8"))
    lene = sCliente.send(salida)
    print("Se han enviado: {} bytes al servidor.".format(lene))   
    ins = sCliente.recv(512)
    insd = ins.decode("UTF8")
    print("Servidor retorna:", insd)
    if inp == "exit":
        break

sCliente.close()
print("Terminado")

'''
import sys
import socket
# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect the socket to the port where the server is listening
server_address = ('localhost', 6000)
print('connecting to {} port {}'.format(*server_address))
sock.connect(server_address)

try:
    peticion ="s"
    while peticion == "s" or peticion == "S":
        operacion = input("\nEnvia una operacion al servidor: ")
        
        sock.send(operacion.encode('UTF-8'))
        print(operacion)
        
  
        resultado = sock.recv(1024).decode()
        #resultado_decode = resultado.decode("utf-8")
        print("El resultado es: ", resultado)
        
        
        peticion = input("\nAprete 's' para realizar otra operacion o cualquier otra letra para finalizar: ")
        sock.sendall(peticion.encode('UTF-8'))
        
finally:
    print('closing socket')
    sock.close()
    '''
    
    #       sock.sendall(message)
    #       sock.recv(16)
    # sck.send(txt.encode("ascii"))