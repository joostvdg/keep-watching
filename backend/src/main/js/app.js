'use strict';

/*jshint esversion: 6 */

import React from 'react';
import ReactDOM from 'react-dom';
import {
    BrowserRouter as Router,
    Route,
    Redirect
} from 'react-router-dom'

import Grid from 'react-bootstrap/lib/Grid';
import Navbar from 'react-bootstrap/lib/Navbar'
import Nav from 'react-bootstrap/lib/Nav'
import NavItem from 'react-bootstrap/lib/NavItem'
import LinkContainer from 'react-router-bootstrap/lib/LinkContainer'
import Glyphicon from 'react-bootstrap/lib/Glyphicon'

import {ShowWatcher} from './watcher/watcher';
import {ShowMovieList} from './movies/movies.js';
import {ShowWatchLists} from './watchlist/watchlist';

const rest = require('rest');
const mime = require('rest/interceptor/mime');


function App(props) {
    return (<MainPage name="Joost"/>);
}

class Home extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            authenticated: false
        };
    }

    componentDidMount(){
        console.log("Home Did Mount");
        let client = rest.wrap(mime);
        client({ path: '/authenticated',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({authenticated: response.entity});
        });
    }

    render(){
        console.log("Home Render -- authenticated: " + this.state.authenticated);
        if (this.state.authenticated) {
            return (
                <div className="container authenticated" >
                    <p>Authenticated</p>
                    <div>
                        <button onClick="logout()" className="btn btn-primary">Logout</button>
                    </div>
                </div>
            );
        } else {
            return (
                <div className="container unauthenticated">
                    <div>
                        With GitHub: <a href="/oauth2/authorization/github">click here</a>
                    </div>
                </div>
            );
        }
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
        console.log("Navigation Render -- authenticated: " + this.state.authenticated);
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
                            <LinkContainer to="/watchlist"><NavItem eventKey={6}>Watchlists</NavItem></LinkContainer>
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

    componentDidMount() {
        console.log("MainPage Did Mount");
    }

    render(){
        // TODO: introduce default watchlist so we can have the movies page init with this
        return (
            <Grid >
                <Router >
                    <div>
                        <Navigation />
                        <Route path="/logout" component={Logout}/>
                        <Route exact path="/" component={Home}/>
                        <Route path="/movies"  component={ShowMovieList}/>
                        <Route path="/profile" component={ShowWatcher}/>
                        <Route path="/watchlist" component={ShowWatchLists}/>
                    </div>
                </Router>
            </Grid>
        );
    }
}

// Render the APP itself
const app = <App name="Keep-Watching"/>;
ReactDOM.render(app, document.getElementById('react'));

// setInterval(app, 100);
