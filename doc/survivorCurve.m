y=-4:0.08:4;
x=2:102;
a=2 + (-2.^(1./x));
plot((1.-atan(y)/2).*a);
title('survivor curve');
grid('on');