
#install.packages('RMySQL',type='source')
#install.packages('RMySQL')
#install.packages('ggplot2')
#install.packages('corrplot')
#install.packages('corrgram')

library(DBI)
library(ggplot2)
library(corrplot)

#database fetch
dataBaseObj<-RMySQL::MySQL()

conn<-dbConnect(dataBaseObj,user="root", password="root", 
                dbname="miningdata", host="localhost")
vehicle_data_frame<-dbGetQuery(conn,"select price,mileage,make,model,trim,type,cylinders,liters,doors,if(cruise=0,0,1) cruise,if(sound=0,0,1) sound,if(leather=0,0,1) leather from vehicle_prices;")

on.exit(dbDisconnect(conn,dataBaseObj))

#print result
summary(vehicle_data_frame)

attach(vehicle_data_frame)

#open pdf for plotting capture
filpath<-"./"
pdf( paste(filpath,"data_output.pdf",sep="/"), height=11, width=8.5)

#plottings
pairs(~cylinders+mileage+liters+doors+price,col='blue',cex=1,diag.panel=panel.hist,panel=panel.smooth,main="pairs plot between numerical data sets")

#plottings - R ^2 value and p-values and anova
lmDataExtract<-  function(lm){
  out <- c(lm$coefficients[1],
           lm$coefficients[2],
           length(lm$model$y),
           summary(lm)$coefficients[2,2],
           pf(summary(lm)$fstatistic[1], summary(lm)$fstatistic[2],
              summary(lm)$fstatistic[3], lower.tail = FALSE),
           summary(lm)$r.squared,summary(lm)$adj.r.squared)
  names(out) <- c("intercept","slope","n","slope.SE","p.value","r.squared","adj.r.squared")
  return(out)
}

#data structure to hold results
colClasses = c("character", "numeric","numeric")
col.names = c("lm-fits", "p_value","r_squared_adjusted_value")

resultTable<-read.table(text="",colClasses = colClasses,col.names = col.names )

dataSets<-vehicle_data_frame

for(j in c('make','type','trim','model')){
  #print(j)
  for(i in unique(dataSets[j])){
    #print(i)
    dataSets[i]<-0
  }  
}

for(j in c('make','trim','type','model')){
  for(i in 1:nrow(dataSets)){
    dataSets[i,dataSets[i,j]]<-1
  }
}

summary(dataSets)
formulaLits<-c("price~mileage","price~cylinders","price~make","price~liters","price~leather","price~sound","price~Chevrolet","price~Chevrolet+cylinders","price~Chevrolet+liters","price~Chevrolet+cylinders+AVEO","price~Chevrolet+AVEO+Coupe+Hatchback+cylinders+liters+mileage+sound+cruise+leather","price~make+cylinders+trim+type","price~make+cylinders+model+trim+type+mileage+sound+cruise+leather")
sink("./linar-model.txt")
row<-1
for(lmIndex  in formulaLits){
  formulaObj<-as.formula(lmIndex)
  lmObj<-lm(formulaObj,data=dataSets)
  objLmExtrct<-lmDataExtract(lmObj)
  par(mfrow = c(2, 2),cex=.8,mfcol = c(2,2))
  plot(lm(lmObj ,data=dataSets),col='blue',main=lmIndex,cex=.57)
  print(lmIndex)
  print(summary( lmObj))
  resultTable[row,]<-c(paste(lmIndex) ,as.numeric(objLmExtrct["p.value"]),as.numeric(objLmExtrct["adj.r.squared"]))
  #anovaDetails[row,] <-c(anova(lmObj))
  row<-row+1 
}
sink()
sink("./anova.txt")
row<-1
for(i in 1:length(formulaLits)){
  formulaObj_1<-as.formula(formulaLits[[i]])
  lmObj_1<-lm(formulaObj_1,data=dataSets)
  j<-i+1
  while(j<=length(formulaLits)){
    #print(j)
    formulaObj_2<-as.formula(formulaLits[[j]])
    
    lmObj_2<-lm(formulaObj_2,data=dataSets)
    
    print(anova(lmObj_1,lmObj_2) )
    j<-j+1
  }
  # print(i)
}
sink()
resultTable

write.table(resultTable,file="./linear model 2.txt")

summary(lm(price~cylinders))$fstatistic
names(summary(lm(price~cylinders)))

#####graph and charts part
#actual plottings of all data sets

# png( paste(filpath,"Rplot%03d.png",sep="/"),
#     width = 480, height = 480, units = "px", pointsize = 12,
#     bg = "white",  res = NA, ...,
#     type = c("cairo", "cairo-png", "Xlib", "quartz"), antialias)



#assoiation between categorical data
mosaicplot(table(make,trim), col = TRUE, las = 2, cex.axis = 0.35,color=factor(trim))

barplot(table(make,trim), beside = TRUE, args.legend = list(title = "make", x = "topright", cex = .7,col=factor(trim)),legend.text = TRUE,col=unique(factor(trim)), cex.names = 0.8,main = "bar plot make and trim")

#numerical data vs categorical data
boxplot(price ~ make,cex.axis = 0.8,varwidth = TRUE,col = unique(factor(make)),main="box lot between price and make",xlab='make',ylab='price')

#correlation data
#price,mileage,make,model,trim,type,cylinders,liters,doors,cruise,sound,leather
correlation_matrix<-data.frame(vehicle_data_frame)
correlation_matrix$price<-price
correlation_matrix$mileage<-mileage
correlation_matrix$make<-as.numeric(factor(make))
correlation_matrix$model<-as.numeric(factor(model))
correlation_matrix$trim<-as.numeric(factor(trim))
correlation_matrix$type<-as.numeric(factor(type))
correlation_matrix$cylinders<-cylinders
correlation_matrix$liters<-liters
correlation_matrix$doors<-doors
correlation_matrix$cruise<-cruise
correlation_matrix$sound<-sound
correlation_matrix$leather<-leather

matrix<-cor(correlation_matrix)

corrplot(matrix,col=rainbow(100),method='number',order='hclust',addrect = 4)
corrplot(matrix,method='number',order='hclust',addrect = 4,col=rainbow(100),hclust.method='complete')

####Histograms####
hist(price,freq=5,include.lowest=TRUE,right=TRUE,main='Histogram of Price',col='blue')

#using ggplot
ggplot(vehicle_data_frame,aes(x=trim))+geom_histogram(fill='lightgreen')+theme(axis.text.x=element_text(angle=90,hjust=1))
ggplot(vehicle_data_frame,aes(x=type))+geom_histogram(fill='cyan')+theme(axis.text.x=element_text(angle=90,hjust=1))
ggplot(vehicle_data_frame,aes(x=liters))+geom_histogram(fill='violet')+theme(axis.text.x=element_text(angle=90,hjust=1))
ggplot(vehicle_data_frame,aes(x=make))+geom_histogram(fill='violet')+theme(axis.text.x=element_text(angle=90,hjust=1))
ggplot(vehicle_data_frame,aes(x=model))+geom_histogram(fill='lightblue')+theme(axis.text.x=element_text(angle=90,hjust=1))
ggplot(vehicle_data_frame,aes(x=cylinders))+geom_histogram(fill='red')+theme(axis.text.x=element_text(angle=90,hjust=1))
ggplot(vehicle_data_frame,aes(x=doors))+geom_histogram(fill='brown')+theme(axis.text.x=element_text(angle=90,hjust=1))
ggplot(vehicle_data_frame,aes(x=mileage))+geom_histogram(fill='blue')+theme(axis.text.x=element_text(angle=90,hjust=1))
#using ggplot
qplot(data=vehicle_data_frame,x=price,binwidth=(max(price)-min(price))/20,col=make,ylab='Frequency',main='histogram for price with make')
qplot(data=vehicle_data_frame,x=price,binwidth=(max(price)-min(price))/20,col=type,ylab='Frequency',main='histogram for price with type')
qplot(data=vehicle_data_frame,x=price,binwidth=(max(price)-min(price))/20,col=model,ylab='Frequency',main='histogram for price with model')

qplot(data=vehicle_data_frame,x=price,col=type,facets=type~make,ylab='Frequency',main='histogram for Price w.r.t type and make')+theme(axis.text.x=element_text(angle=90,hjust=1,vjust=.5))
qplot(data=vehicle_data_frame,x=price,col=type,facets=cylinders~make,ylab='Frequency',main='histogram for Price w.r.t make and cylinders')+theme(axis.text.x=element_text(angle=90,hjust=1,vjust=.5))

qplot(data=vehicle_data_frame,x=mileage,col=type,facets=type~make,ylab='Frequency',main='histogram for Price w.r.t type and make')+theme(axis.text.x=element_text(angle=90,hjust=1,vjust=.5))
qplot(data=vehicle_data_frame,x=mileage,col=type,ylab='Frequency',main='histogram for mileage with type')

qplot(data=vehicle_data_frame,x=liters,col=type,facets=type~make,ylab='Frequency',main='histogram for liter with type')

qplot(data=vehicle_data_frame,x=make,col=type,ylab='Frequency',main='histogram for make with type')
qplot(data=vehicle_data_frame,x=cylinders,col=make,facets=type~liters,ylab='Frequency',main='histogram for Cylinders w.r.t make, types and liters')+theme(axis.text.x=element_text(angle=90,hjust=1,vjust=.5))
qplot(data=vehicle_data_frame,x=cylinders,col=type,ylab='Frequency',main='histogram for Cylinders with type')
qplot(data=vehicle_data_frame,x=trim,binwidth=100,col=make,ylab='Frequency',main='histogram for trim with make')+theme(axis.text.x=element_text(angle=90,hjust=1))
qplot(data=vehicle_data_frame,x=doors,col=type,ylab='Frequency',main='histogram for Doors')
qplot(data=vehicle_data_frame,x=cruise,col=type,ylab='Frequency',main='histogram for Cruise with type')
qplot(data=vehicle_data_frame,x=sound,col=type,ylab='Frequency',main='histogram for Sound with type')
qplot(data=vehicle_data_frame,x=leather,col=type,ylab='Frequency',main='histogram for Leather with type')

#anova and lm output in graphical format
p<-ggplot(resultTable,aes(x=p_value,y=r_squared_adjusted_value))+geom_point(size=2,sahpe=c(1:13))+ggtitle("Different models comparison ")
p+theme(axis.text.x=element_text(angle=90,hjust=1,vjust=0.5))+geom_text(aes(label=lm.fits),hjust=.09, vjust=-.35,size=3,color=(c(1:length(resultTable$lm.fits)+233)),angle=-25)

#paired correlations
p<-ggplot(vehicle_data_frame,aes(x=cylinders,y=price,color=make))
p+geom_smooth(method=lm,fullrange=T)+geom_point(shape=1)

p<-ggplot(vehicle_data_frame,aes(model,price,color=factor(make)))+geom_point()
p+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=mileage,y=price,color=factor(make)))+geom_point(size=2)
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=type,y=price,color=factor(make)))+geom_point(size=2)
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=trim,y=price,color=factor(make)))+geom_point(size=2)
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=liters,y=price,color=factor(make)))+geom_point(size=2)
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=make,y=mileage,color=factor(type)))+geom_point(size=2)
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=model,y=mileage,color=factor(make)))+geom_point(size=2)
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=trim,y=mileage,color=factor(make)))+geom_point(size=2)
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=liters,y=cylinders,color=factor(make)))+geom_point(size=2)
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=liters,y=cylinders,color=factor(type)))+geom_point(size=2)
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=cylinders,y=mileage,color=price))+geom_point()
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))

p<-ggplot(vehicle_data_frame,aes(x=mileage,y=price,color=liters))+geom_point()
p+geom_smooth(method=lm,size=1)+theme(axis.text.x=element_text(angle=90,hjust=1))


dev.off()
