import React from 'react';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';
import { Link } from 'react-router-dom';

const NavBar: React.FC = () => {
    return (
        <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
            <Toolbar>
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    Garden Simulation
                </Typography>
                <Button color="inherit" component={Link} to="/">
                    Dashboard
                </Button>
                <Button color="inherit" component={Link} to="/parcels">
                    Parcels
                </Button>
                <Button color="inherit" component={Link} to="/simulation">
                    Simulation
                </Button>
            </Toolbar>
        </AppBar>
    );
};

export default NavBar;