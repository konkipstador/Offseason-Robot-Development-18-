using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Thin_Dashboard.UI_Objects
{
    interface ThinControlInterface
    {
        void update();
        String getType();
        Boolean getAlive();
        int getX();
        int getY();
        Double getSize();
        String getInfo();
        Boolean getUsesNetTables();
    }
}
