'use strict';

/*jshint esversion: 6 */

import React from 'react';
import ReactDOM from 'react-dom';
import {
    BrowserRouter as Router,
    Route,
    Redirect
} from 'react-router-dom'

import Button from 'react-bootstrap/lib/Button';
import Grid from 'react-bootstrap/lib/Grid';
import Navbar from 'react-bootstrap/lib/Navbar'
import Nav from 'react-bootstrap/lib/Nav'
import NavItem from 'react-bootstrap/lib/NavItem'
import LinkContainer from 'react-router-bootstrap/lib/LinkContainer'
import Glyphicon from 'react-bootstrap/lib/Glyphicon'

import {ShowWatcher} from './watcher/watcher';
import {ShowMovieList} from './movies/movies.js';

const rest = require('rest');
const mime = require('rest/interceptor/mime');


function App(props) {
    return (<MainPage name="Joost"/>);
}

class Home extends React.Component {
    render(){
        return (
            <div>
                <p>Welcome!</p>
                <p>
                    <Button href="/view/facebook.html" bsSize="large" bsStyle="primary">Login with Facebook <Glyphicon glyph="log-in"  /></Button>
                </p>
                <p>
                    <Button href="/view/github.html" bsSize="large" bsStyle="success">Login with GitHub <Glyphicon glyph="log-in"  /></Button>
                </p>
            </div>
        );
    }
}

class Logout extends React.Component {

    componentDidMount() {
        console.log("Doing Logout");
        let client = rest.wrap(mime);
        client({ path: '/logout', method: 'POST' }).then(response => {
            window.location.reload();
        });
    }


    render(){
        return (
            <div>
                <p>Logout!</p>
                <Redirect to="/" push />
            </div>
        );
    }
}

function navHeader () {
    return (
        <Navbar.Header>
            <Navbar.Brand>
                Keep-Watching
            </Navbar.Brand>
            <Navbar.Toggle />
        </Navbar.Header>
    );
}

class Navigation extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            authenticated: false
        };
    }

    componentDidMount(){
        console.log("Navigation Did Mount");
        let client = rest.wrap(mime);
        client({ path: '/authenticated',
            headers: {'Accept': 'application/json'}}).then(response => {
            console.log(response);
            this.setState({authenticated: response.entity});
        });
    }

    render(){
        if (this.state.authenticated) {
            return (
                <Navbar collapseOnSelect>
                    <navHeader />
                    <Navbar.Collapse>
                        <Nav >
                            <LinkContainer to="/logout"><NavItem eventKey={3}><Glyphicon glyph="log-out"/></NavItem></LinkContainer>
                            <LinkContainer to="/"><NavItem eventKey={3}><Glyphicon glyph="home"/></NavItem></LinkContainer>
                            <LinkContainer to="/movies"><NavItem eventKey={4}>Movies</NavItem></LinkContainer>
                            <LinkContainer to="/profile"><NavItem eventKey={5}>Profile</NavItem></LinkContainer>
                        </Nav>
                    </Navbar.Collapse>
                </Navbar>
            );
        } else {
            return (
                <Navbar collapseOnSelect>
                    <navHeader />
                    <Navbar.Collapse>
                        <Nav >
                            <LinkContainer to="/"><NavItem eventKey={3}><Glyphicon glyph="home"/></NavItem></LinkContainer>
                        </Nav>
                    </Navbar.Collapse>
                </Navbar>
            );
        }
    }
}

class MainPage extends React.Component {

    render(){
        return (
            <Grid >
                <Router >
                    <div>
                        <Navigation />
                        <Route path="/logout" component={Logout}/>
                        <Route exact path="/" component={Home}/>
                        <Route path="/movies" component={ShowMovieList}/>
                        <Route path="/profile" component={ShowWatcher}/>
                    </div>
                </Router>
            </Grid>
        );
    }
}

// Render the APP itself
const app = <App name="Joost"/>;
ReactDOM.render(app, document.getElementById('react'));

// setInterval(app, 100);
